import { useEffect, useState, useCallback } from 'react';

interface Issue {
  number: number;
  title: string;
  state: string;
  labels: { name: string; color: string }[];
  created_at: string;
  updated_at: string;
  assignee?: { login: string };
  body?: string;
}

interface Stats {
  todo: number;
  inProgress: number;
  done: number;
  failed: number;
  total: number;
  tierA: number;
  tierB: number;
}

const REPO = 'A2OH/harmony-android-guide';
const API_BASE = 'https://api.github.com/repos/' + REPO;

async function fetchAllShimIssues(): Promise<Issue[]> {
  // Fetch all pages
  let page = 1;
  let all: Issue[] = [];
  while (true) {
    const params = new URLSearchParams({
      per_page: '100',
      state: 'all',
      labels: 'shim',
      page: String(page),
    });
    const res = await fetch(`${API_BASE}/issues?${params}`);
    if (!res.ok) break;
    const batch: Issue[] = await res.json();
    if (batch.length === 0) break;
    all = all.concat(batch);
    if (batch.length < 100) break;
    page++;
  }
  return all;
}

function hasLabel(issue: Issue, name: string): boolean {
  return issue.labels.some(l => l.name === name);
}

function getStatus(issue: Issue): string {
  if (hasLabel(issue, 'done') || issue.state === 'closed') return 'done';
  if (hasLabel(issue, 'failed')) return 'failed';
  if (hasLabel(issue, 'in-progress')) return 'in-progress';
  return 'todo';
}

function getTier(issue: Issue): string {
  if (hasLabel(issue, 'tier-a')) return 'A';
  if (hasLabel(issue, 'tier-b')) return 'B';
  if (hasLabel(issue, 'tier-c')) return 'C';
  return '?';
}

function timeSince(dateStr: string): string {
  const seconds = Math.floor((Date.now() - new Date(dateStr).getTime()) / 1000);
  if (seconds < 60) return `${seconds}s ago`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
  if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
  return `${Math.floor(seconds / 86400)}d ago`;
}

function extractClassName(title: string): string {
  return title.replace('[SHIM] Implement ', '');
}

const STATUS_COLORS: Record<string, string> = {
  'todo': 'bg-gray-700 text-gray-300',
  'in-progress': 'bg-yellow-900/50 text-yellow-300 border border-yellow-700/50',
  'done': 'bg-green-900/50 text-green-300 border border-green-700/50',
  'failed': 'bg-red-900/50 text-red-300 border border-red-700/50',
};

const STATUS_DOT: Record<string, string> = {
  'todo': 'bg-gray-500',
  'in-progress': 'bg-yellow-400 animate-pulse',
  'done': 'bg-green-400',
  'failed': 'bg-red-400',
};

export default function Orchestrator() {
  const [issues, setIssues] = useState<Issue[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<string>('all');
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());
  const [autoRefresh, setAutoRefresh] = useState(true);

  const refresh = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchAllShimIssues();
      setIssues(data);
      setLastRefresh(new Date());
    } catch (e) {
      setError('Failed to fetch issues. GitHub API rate limit?');
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  // Auto-refresh every 30 seconds
  useEffect(() => {
    if (!autoRefresh) return;
    const interval = setInterval(refresh, 30000);
    return () => clearInterval(interval);
  }, [autoRefresh, refresh]);

  const stats: Stats = {
    todo: issues.filter(i => getStatus(i) === 'todo').length,
    inProgress: issues.filter(i => getStatus(i) === 'in-progress').length,
    done: issues.filter(i => getStatus(i) === 'done').length,
    failed: issues.filter(i => getStatus(i) === 'failed').length,
    total: issues.length,
    tierA: issues.filter(i => hasLabel(i, 'tier-a')).length,
    tierB: issues.filter(i => hasLabel(i, 'tier-b')).length,
  };

  const filtered = issues.filter(i => {
    if (filter === 'all') return true;
    return getStatus(i) === filter;
  }).sort((a, b) => {
    // Sort: in-progress first, then todo, then done, then failed
    const order: Record<string, number> = { 'in-progress': 0, 'todo': 1, 'failed': 2, 'done': 3 };
    const diff = (order[getStatus(a)] ?? 4) - (order[getStatus(b)] ?? 4);
    if (diff !== 0) return diff;
    return a.number - b.number;
  });

  const completionPct = stats.total > 0 ? Math.round((stats.done / stats.total) * 100) : 0;

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Shim Orchestrator</h1>
        <div className="flex items-center gap-3">
          <label className="flex items-center gap-1.5 text-sm text-gray-400">
            <input
              type="checkbox"
              checked={autoRefresh}
              onChange={e => setAutoRefresh(e.target.checked)}
              className="rounded"
            />
            Auto-refresh
          </label>
          <button
            onClick={refresh}
            disabled={loading}
            className="px-3 py-1.5 bg-gray-800 border border-gray-700 rounded-lg text-sm hover:bg-gray-700 disabled:opacity-50"
          >
            {loading ? 'Refreshing...' : 'Refresh'}
          </button>
          <span className="text-xs text-gray-600">
            Updated {timeSince(lastRefresh.toISOString())}
          </span>
        </div>
      </div>

      {error && (
        <div className="bg-red-900/30 border border-red-700/50 rounded-lg p-3 text-red-300 text-sm">
          {error}
        </div>
      )}

      {/* Progress bar */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
        <div className="flex items-center justify-between mb-3">
          <span className="text-lg font-semibold">Overall Progress</span>
          <span className="text-2xl font-bold text-green-400">{completionPct}%</span>
        </div>
        <div className="h-4 bg-gray-800 rounded-full overflow-hidden flex">
          {stats.done > 0 && (
            <div
              className="h-full bg-green-500 transition-all duration-500"
              style={{ width: `${(stats.done / stats.total) * 100}%` }}
              title={`${stats.done} done`}
            />
          )}
          {stats.inProgress > 0 && (
            <div
              className="h-full bg-yellow-500 transition-all duration-500"
              style={{ width: `${(stats.inProgress / stats.total) * 100}%` }}
              title={`${stats.inProgress} in progress`}
            />
          )}
          {stats.failed > 0 && (
            <div
              className="h-full bg-red-500 transition-all duration-500"
              style={{ width: `${(stats.failed / stats.total) * 100}%` }}
              title={`${stats.failed} failed`}
            />
          )}
        </div>
        <div className="flex gap-6 mt-3 text-sm">
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-green-500" /> {stats.done} done
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-yellow-500 animate-pulse" /> {stats.inProgress} in progress
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-gray-500" /> {stats.todo} todo
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-red-500" /> {stats.failed} failed
          </span>
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        <StatCard label="Total Tasks" value={stats.total} />
        <StatCard label="Tier A (Pure Java)" value={stats.tierA} />
        <StatCard label="Tier B (I/O)" value={stats.tierB} />
        <StatCard label="Completed" value={stats.done} color="text-green-400" />
        <StatCard label="Active Workers" value={stats.inProgress} color="text-yellow-400" />
      </div>

      {/* Filter tabs */}
      <div className="flex gap-2">
        {['all', 'todo', 'in-progress', 'done', 'failed'].map(f => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`px-3 py-1.5 rounded-lg text-sm ${
              filter === f
                ? 'bg-blue-600 text-white'
                : 'bg-gray-800 text-gray-400 hover:text-white'
            }`}
          >
            {f === 'all' ? `All (${stats.total})` :
             f === 'todo' ? `Todo (${stats.todo})` :
             f === 'in-progress' ? `In Progress (${stats.inProgress})` :
             f === 'done' ? `Done (${stats.done})` :
             `Failed (${stats.failed})`}
          </button>
        ))}
      </div>

      {/* Issue list */}
      <div className="bg-gray-900 rounded-xl border border-gray-800 overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-gray-400 border-b border-gray-800 text-left">
              <th className="py-2 px-3 w-12">#</th>
              <th className="py-2 px-3">Class</th>
              <th className="py-2 px-3 w-20">Tier</th>
              <th className="py-2 px-3 w-28">Status</th>
              <th className="py-2 px-3 w-28">Updated</th>
              <th className="py-2 px-3 w-32">Worker</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(issue => {
              const status = getStatus(issue);
              return (
                <tr key={issue.number} className="border-b border-gray-800/50 hover:bg-gray-800/30">
                  <td className="py-2 px-3 text-gray-500 font-mono">{issue.number}</td>
                  <td className="py-2 px-3">
                    <a
                      href={`https://github.com/${REPO}/issues/${issue.number}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-400 hover:underline"
                    >
                      {extractClassName(issue.title)}
                    </a>
                  </td>
                  <td className="py-2 px-3">
                    <span className="text-xs bg-gray-800 px-2 py-0.5 rounded">
                      {getTier(issue)}
                    </span>
                  </td>
                  <td className="py-2 px-3">
                    <span className={`inline-flex items-center gap-1.5 text-xs px-2 py-0.5 rounded ${STATUS_COLORS[status]}`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${STATUS_DOT[status]}`} />
                      {status}
                    </span>
                  </td>
                  <td className="py-2 px-3 text-gray-500 text-xs">
                    {timeSince(issue.updated_at)}
                  </td>
                  <td className="py-2 px-3 text-gray-500 text-xs">
                    {issue.assignee?.login || (status === 'in-progress' ? 'claiming...' : '-')}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="p-8 text-center text-gray-500">
            {loading ? 'Loading issues...' : 'No issues found'}
          </div>
        )}
      </div>

      {/* How to contribute */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
        <h2 className="text-lg font-semibold mb-3">How to Contribute (CC Worker)</h2>
        <div className="bg-gray-800 rounded-lg p-4 font-mono text-xs text-gray-300 overflow-x-auto whitespace-pre">{`# 1. Clone and setup
git clone https://github.com/${REPO}.git && cd harmony-android-guide

# 2. Pick a task
gh issue list --repo ${REPO} --label todo --label tier-a --limit 5

# 3. Claim it
gh issue edit <NUMBER> --repo ${REPO} --remove-label todo --add-label in-progress

# 4. Implement the shim + write tests (see CLAUDE.md for details)

# 5. Verify
cd test-apps && ./run-local-tests.sh headless

# 6. Push and close
git checkout -b shim/<class-name> && git push origin shim/<class-name>
gh issue close <NUMBER> --comment "Done" --repo ${REPO}`}</div>
      </div>
    </div>
  );
}

function StatCard({ label, value, color = 'text-white' }: { label: string; value: number; color?: string }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
      <div className="text-xs text-gray-500 uppercase tracking-wide">{label}</div>
      <div className={`text-2xl font-bold mt-1 ${color}`}>{value}</div>
    </div>
  );
}
