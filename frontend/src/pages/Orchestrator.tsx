import { useEffect, useState, useCallback, useRef } from 'react';

interface Issue {
  number: number;
  title: string;
  state: string;
  labels: { name: string; color: string }[];
  created_at: string;
  updated_at: string;
  assignee?: { login: string };
  body?: string;
  html_url?: string;
}

interface Stats {
  todo: number;
  inProgress: number;
  done: number;
  failed: number;
  total: number;
  tierA: number;
  tierB: number;
  tierC: number;
  tierD: number;
}

const REPO = 'A2OH/harmony-android-guide';
const API_BASE = 'https://api.github.com/repos/' + REPO;

// ─── Tier class definitions ─────────────────────────────────────────────────
interface TierClass {
  pkg: string;
  cls: string;
  desc: string;
}

const TIER_CLASSES: Record<string, { label: string; color: string; classes: TierClass[] }> = {
  a: {
    label: 'Tier A — Pure Java',
    color: 'text-blue-400',
    classes: [
      { pkg: 'android.os', cls: 'Bundle', desc: 'HashMap-backed key-value store' },
      { pkg: 'android.os', cls: 'BaseBundle', desc: 'Base class for Bundle' },
      { pkg: 'android.os', cls: 'PersistableBundle', desc: 'Bundle that survives process death' },
      { pkg: 'android.os', cls: 'Message', desc: 'Handler message with what/arg1/arg2/obj' },
      { pkg: 'android.os', cls: 'Parcel', desc: 'Serialization container (pure Java subset)' },
      { pkg: 'android.os', cls: 'PatternMatcher', desc: 'String pattern matching (LITERAL, PREFIX, GLOB)' },
      { pkg: 'android.os', cls: 'ResultReceiver', desc: 'Callback wrapper' },
      { pkg: 'android.content', cls: 'Intent', desc: 'Action/data/extras/component/categories' },
      { pkg: 'android.content', cls: 'IntentFilter', desc: 'Action/category/data matching' },
      { pkg: 'android.content', cls: 'ContentValues', desc: 'Map<String,Object> for DB inserts' },
      { pkg: 'android.content', cls: 'ComponentName', desc: 'Package + class name pair' },
      { pkg: 'android.content', cls: 'ContentUris', desc: 'Uri helper: withAppendedId, parseId' },
      { pkg: 'android.content', cls: 'UriMatcher', desc: 'Tree-based URI routing' },
      { pkg: 'android.net', cls: 'Uri', desc: 'Immutable URI with parse/build/query' },
      { pkg: 'android.net', cls: 'UrlQuerySanitizer', desc: 'Query parameter sanitization' },
      { pkg: 'android.util', cls: 'SparseArray', desc: 'int->Object map (no boxing)' },
      { pkg: 'android.util', cls: 'SparseBooleanArray', desc: 'int->boolean map' },
      { pkg: 'android.util', cls: 'SparseIntArray', desc: 'int->int map' },
      { pkg: 'android.util', cls: 'SparseLongArray', desc: 'int->long map' },
      { pkg: 'android.util', cls: 'ArrayMap', desc: 'Memory-efficient Map<K,V>' },
      { pkg: 'android.util', cls: 'ArraySet', desc: 'Memory-efficient Set<E>' },
      { pkg: 'android.util', cls: 'Base64', desc: 'Base64 encode/decode' },
      { pkg: 'android.util', cls: 'Pair', desc: 'Immutable pair (first, second)' },
      { pkg: 'android.util', cls: 'Size', desc: 'Immutable width x height (int)' },
      { pkg: 'android.util', cls: 'SizeF', desc: 'Immutable width x height (float)' },
      { pkg: 'android.util', cls: 'TypedValue', desc: 'Resource value with type/data/density' },
      { pkg: 'android.util', cls: 'Log', desc: 'Logging (println to stdout)' },
      { pkg: 'android.util', cls: 'Patterns', desc: 'Common regex patterns (EMAIL, URL, IP)' },
      { pkg: 'android.util', cls: 'JsonReader', desc: 'Streaming JSON parser' },
      { pkg: 'android.util', cls: 'JsonWriter', desc: 'Streaming JSON writer' },
      { pkg: 'android.util', cls: 'Xml', desc: 'XML pull parser factory' },
      { pkg: 'android.util', cls: 'LruCache', desc: 'Least-recently-used cache' },
      { pkg: 'android.text', cls: 'TextUtils', desc: 'isEmpty, join, split, htmlEncode, etc.' },
      { pkg: 'android.text', cls: 'SpannableString', desc: 'String with span markup' },
      { pkg: 'android.text', cls: 'SpannableStringBuilder', desc: 'Mutable spannable string' },
      { pkg: 'android.text', cls: 'Html', desc: 'HTML to/from Spanned' },
      { pkg: 'android.graphics', cls: 'Color', desc: 'ARGB color int utilities' },
      { pkg: 'android.graphics', cls: 'Point', desc: 'Immutable int x,y' },
      { pkg: 'android.graphics', cls: 'PointF', desc: 'Immutable float x,y' },
      { pkg: 'android.graphics', cls: 'Rect', desc: 'int rectangle' },
      { pkg: 'android.graphics', cls: 'RectF', desc: 'float rectangle' },
      { pkg: 'android.graphics', cls: 'Matrix', desc: '3x3 transformation matrix' },
      { pkg: 'android.database', cls: 'MatrixCursor', desc: 'In-memory Cursor from arrays' },
      { pkg: 'android.database', cls: 'MergeCursor', desc: 'Concatenates multiple Cursors' },
      { pkg: 'android.database', cls: 'DatabaseUtils', desc: 'SQL utility methods' },
    ],
  },
  b: {
    label: 'Tier B — I/O with Java Fallback',
    color: 'text-yellow-400',
    classes: [
      { pkg: 'android.content', cls: 'SharedPreferences', desc: 'Key-value prefs (HashMap + file)' },
      { pkg: 'android.database.sqlite', cls: 'SQLiteDatabase', desc: 'SQL database (needs JDBC or stub)' },
      { pkg: 'android.database.sqlite', cls: 'SQLiteOpenHelper', desc: 'DB lifecycle manager' },
      { pkg: 'android.os', cls: 'Environment', desc: 'External storage dirs (File-based)' },
      { pkg: 'android.os', cls: 'StatFs', desc: 'Filesystem stats' },
      { pkg: 'android.os', cls: 'Handler', desc: 'Message queue + runnable dispatch' },
      { pkg: 'android.os', cls: 'Looper', desc: 'Thread message loop' },
      { pkg: 'android.os', cls: 'HandlerThread', desc: 'Thread with Looper' },
      { pkg: 'android.os', cls: 'AsyncTask', desc: 'Background thread + UI callback' },
      { pkg: 'android.os', cls: 'CountDownTimer', desc: 'Periodic tick timer' },
      { pkg: 'android.os', cls: 'SystemClock', desc: 'uptimeMillis, elapsedRealtime' },
      { pkg: 'android.content', cls: 'ContentProvider', desc: 'CRUD interface' },
      { pkg: 'android.content', cls: 'ContentResolver', desc: 'Client to ContentProvider' },
      { pkg: 'android.util', cls: 'AtomicFile', desc: 'Atomic file write with backup' },
      { pkg: 'android.app', cls: 'Application', desc: 'App singleton' },
    ],
  },
  c: {
    label: 'Tier C — System Services (needs OHBridge)',
    color: 'text-orange-400',
    classes: [
      { pkg: 'android.app', cls: 'NotificationManager', desc: 'Notification posting and management' },
      { pkg: 'android.app', cls: 'AlarmManager', desc: 'Scheduled alarm delivery' },
      { pkg: 'android.app', cls: 'DownloadManager', desc: 'HTTP download management' },
      { pkg: 'android.app', cls: 'KeyguardManager', desc: 'Keyguard and lock screen' },
      { pkg: 'android.app', cls: 'ActivityManager', desc: 'Activity and process info' },
      { pkg: 'android.app', cls: 'PendingIntent', desc: 'Deferred intent delivery' },
      { pkg: 'android.location', cls: 'LocationManager', desc: 'GPS / network location' },
      { pkg: 'android.media', cls: 'MediaPlayer', desc: 'Audio/video playback' },
      { pkg: 'android.media', cls: 'AudioManager', desc: 'Volume and audio routing' },
      { pkg: 'android.media', cls: 'MediaRecorder', desc: 'Audio/video recording' },
      { pkg: 'android.net', cls: 'ConnectivityManager', desc: 'Network connectivity' },
      { pkg: 'android.net.wifi', cls: 'WifiManager', desc: 'WiFi management' },
      { pkg: 'android.telephony', cls: 'TelephonyManager', desc: 'Phone state and info' },
      { pkg: 'android.hardware', cls: 'SensorManager', desc: 'Device sensors' },
      { pkg: 'android.hardware', cls: 'Camera', desc: 'Camera access (deprecated)' },
      { pkg: 'android.hardware.camera2', cls: 'CameraManager', desc: 'Camera2 API' },
      { pkg: 'android.bluetooth', cls: 'BluetoothAdapter', desc: 'Bluetooth management' },
      { pkg: 'android.bluetooth', cls: 'BluetoothManager', desc: 'BLE management' },
      { pkg: 'android.content.pm', cls: 'PackageManager', desc: 'App package info' },
      { pkg: 'android.accounts', cls: 'AccountManager', desc: 'User accounts' },
    ],
  },
  d: {
    label: 'Tier D — UI Components (needs ArkUI)',
    color: 'text-red-400',
    classes: [
      { pkg: 'android.app', cls: 'Activity', desc: 'Screen lifecycle container' },
      { pkg: 'android.app', cls: 'Fragment', desc: 'Modular UI section' },
      { pkg: 'android.app', cls: 'Dialog', desc: 'Popup dialog window' },
      { pkg: 'android.app', cls: 'AlertDialog', desc: 'Standard alert dialog' },
      { pkg: 'android.app', cls: 'Service', desc: 'Background service' },
      { pkg: 'android.view', cls: 'View', desc: 'Base UI element' },
      { pkg: 'android.view', cls: 'ViewGroup', desc: 'Container for views' },
      { pkg: 'android.widget', cls: 'TextView', desc: 'Text display' },
      { pkg: 'android.widget', cls: 'EditText', desc: 'Text input' },
      { pkg: 'android.widget', cls: 'Button', desc: 'Click button' },
      { pkg: 'android.widget', cls: 'ImageView', desc: 'Image display' },
      { pkg: 'android.widget', cls: 'ListView', desc: 'Scrollable list' },
      { pkg: 'android.widget', cls: 'RecyclerView', desc: 'Efficient scrollable list' },
      { pkg: 'android.widget', cls: 'Toast', desc: 'Transient popup message' },
      { pkg: 'android.webkit', cls: 'WebView', desc: 'Embedded browser' },
    ],
  },
};

// ─── Package → Skill mapping (from api_compat.db android_packages.skill) ────

const PACKAGE_SKILL_MAP: Record<string, string> = {
  'android.app': 'A2OH-LIFECYCLE', 'android.app.admin': 'A2OH-LIFECYCLE',
  'android.app.assist': 'A2OH-LIFECYCLE', 'android.app.backup': 'A2OH-LIFECYCLE',
  'android.app.blob': 'A2OH-LIFECYCLE', 'android.app.job': 'A2OH-LIFECYCLE',
  'android.app.role': 'A2OH-LIFECYCLE', 'android.app.slice': 'A2OH-LIFECYCLE',
  'android.app.usage': 'A2OH-LIFECYCLE', 'android.content': 'A2OH-LIFECYCLE',
  'android.content.pm': 'A2OH-LIFECYCLE', 'android.content.res': 'A2OH-LIFECYCLE',
  'android.content.res.loader': 'A2OH-LIFECYCLE', 'android.accounts': 'A2OH-LIFECYCLE',
  'android.os': 'A2OH-LIFECYCLE', 'android.os.storage': 'A2OH-LIFECYCLE',
  'android.os.health': 'A2OH-LIFECYCLE', 'android.os.strictmode': 'A2OH-LIFECYCLE',
  'android.view': 'A2OH-UI-REWRITE', 'android.view.accessibility': 'A2OH-UI-REWRITE',
  'android.view.animation': 'A2OH-UI-REWRITE', 'android.view.autofill': 'A2OH-UI-REWRITE',
  'android.view.inputmethod': 'A2OH-UI-REWRITE', 'android.widget': 'A2OH-UI-REWRITE',
  'android.widget.inline': 'A2OH-UI-REWRITE', 'android.transition': 'A2OH-UI-REWRITE',
  'android.animation': 'A2OH-UI-REWRITE', 'android.gesture': 'A2OH-UI-REWRITE',
  'android.appwidget': 'A2OH-UI-REWRITE', 'android.preference': 'A2OH-UI-REWRITE',
  'android.inputmethodservice': 'A2OH-UI-REWRITE',
  'android.database': 'A2OH-DATA-LAYER', 'android.database.sqlite': 'A2OH-DATA-LAYER',
  'android.provider': 'A2OH-DATA-LAYER',
  'android.hardware': 'A2OH-DEVICE-API', 'android.hardware.biometrics': 'A2OH-DEVICE-API',
  'android.hardware.camera2': 'A2OH-DEVICE-API', 'android.hardware.camera2.params': 'A2OH-DEVICE-API',
  'android.hardware.display': 'A2OH-DEVICE-API', 'android.hardware.fingerprint': 'A2OH-DEVICE-API',
  'android.hardware.input': 'A2OH-DEVICE-API', 'android.hardware.usb': 'A2OH-DEVICE-API',
  'android.bluetooth': 'A2OH-DEVICE-API', 'android.bluetooth.le': 'A2OH-DEVICE-API',
  'android.location': 'A2OH-DEVICE-API', 'android.telephony': 'A2OH-DEVICE-API',
  'android.telephony.gsm': 'A2OH-DEVICE-API', 'android.telecom': 'A2OH-DEVICE-API',
  'android.nfc': 'A2OH-DEVICE-API', 'android.nfc.tech': 'A2OH-DEVICE-API',
  'android.media': 'A2OH-MEDIA', 'android.media.audiofx': 'A2OH-MEDIA',
  'android.media.session': 'A2OH-MEDIA', 'android.media.browse': 'A2OH-MEDIA',
  'android.drm': 'A2OH-MEDIA', 'android.speech': 'A2OH-MEDIA', 'android.speech.tts': 'A2OH-MEDIA',
  'android.net': 'A2OH-NETWORKING', 'android.net.http': 'A2OH-NETWORKING',
  'android.net.wifi': 'A2OH-NETWORKING', 'android.net.wifi.p2p': 'A2OH-NETWORKING',
  'android.net.ssl': 'A2OH-NETWORKING',
  'android.util': 'A2OH-JAVA-TO-ARKTS', 'android.text': 'A2OH-JAVA-TO-ARKTS',
  'android.text.format': 'A2OH-JAVA-TO-ARKTS', 'android.text.style': 'A2OH-JAVA-TO-ARKTS',
  'android.text.util': 'A2OH-JAVA-TO-ARKTS', 'android.annotation': 'A2OH-JAVA-TO-ARKTS',
  'android.graphics': 'A2OH-JAVA-TO-ARKTS', 'android.graphics.drawable': 'A2OH-JAVA-TO-ARKTS',
  'android.graphics.drawable.shapes': 'A2OH-JAVA-TO-ARKTS',
  'android.graphics.fonts': 'A2OH-JAVA-TO-ARKTS', 'android.graphics.pdf': 'A2OH-JAVA-TO-ARKTS',
  'android.icu.text': 'A2OH-JAVA-TO-ARKTS', 'android.icu.util': 'A2OH-JAVA-TO-ARKTS',
  'android.security': 'A2OH-CONFIG', 'android.webkit': 'A2OH-CONFIG',
  'android.print': 'A2OH-CONFIG', 'android.accessibilityservice': 'A2OH-CONFIG',
};

// ─── API helpers ─────────────────────────────────────────────────────────────

async function fetchAllShimIssues(): Promise<Issue[]> {
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

async function ghApiCall(
  endpoint: string,
  method: string,
  token: string,
  body?: Record<string, unknown>,
) {
  const res = await fetch(`${API_BASE}${endpoint}`, {
    method,
    headers: {
      Authorization: `token ${token}`,
      Accept: 'application/vnd.github.v3+json',
      'Content-Type': 'application/json',
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`GitHub API ${res.status}: ${text}`);
  }
  return res.json();
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
  if (hasLabel(issue, 'tier-d')) return 'D';
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

const TIER_COLORS: Record<string, string> = {
  'A': 'bg-blue-900/50 text-blue-300',
  'B': 'bg-yellow-900/50 text-yellow-300',
  'C': 'bg-orange-900/50 text-orange-300',
  'D': 'bg-red-900/50 text-red-300',
  '?': 'bg-gray-800 text-gray-400',
};

// ─── Main component ─────────────────────────────────────────────────────────

export default function Orchestrator() {
  const [issues, setIssues] = useState<Issue[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<string>('all');
  const [tierFilter, setTierFilter] = useState<string>('all');
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());
  const [autoRefresh, setAutoRefresh] = useState(true);

  // Issue management
  const [token, setToken] = useState<string>(() => localStorage.getItem('gh_token') || '');
  const [showTokenInput, setShowTokenInput] = useState(false);
  const [actionLoading, setActionLoading] = useState<number | null>(null);
  const [actionMsg, setActionMsg] = useState<string | null>(null);

  // Batch creation
  const [showCreatePanel, setShowCreatePanel] = useState(false);
  const [createTier, setCreateTier] = useState<string>('b');
  const [selectedClasses, setSelectedClasses] = useState<Set<string>>(new Set());
  const [creating, setCreating] = useState(false);
  const [createLog, setCreateLog] = useState<string[]>([]);

  // Detail panel
  const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null);

  // Single issue creation
  const [showSingleCreate, setShowSingleCreate] = useState(false);
  const [singleClassName, setSingleClassName] = useState('');
  const [singleTier, setSingleTier] = useState('a');
  const [singleDesc, setSingleDesc] = useState('');
  const [singleCreating, setSingleCreating] = useState(false);

  const saveToken = (t: string) => {
    setToken(t);
    localStorage.setItem('gh_token', t);
    setShowTokenInput(false);
  };

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

  useEffect(() => { refresh(); }, [refresh]);

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
    tierC: issues.filter(i => hasLabel(i, 'tier-c')).length,
    tierD: issues.filter(i => hasLabel(i, 'tier-d')).length,
  };

  const filtered = issues.filter(i => {
    if (filter !== 'all' && getStatus(i) !== filter) return false;
    if (tierFilter !== 'all' && getTier(i) !== tierFilter) return false;
    return true;
  }).sort((a, b) => {
    const order: Record<string, number> = { 'in-progress': 0, 'todo': 1, 'failed': 2, 'done': 3 };
    const diff = (order[getStatus(a)] ?? 4) - (order[getStatus(b)] ?? 4);
    if (diff !== 0) return diff;
    return a.number - b.number;
  });

  const completionPct = stats.total > 0 ? Math.round((stats.done / stats.total) * 100) : 0;

  // ─── Issue actions ──────────────────────────────────────────────────────

  const requireToken = (): string | null => {
    if (!token) {
      setShowTokenInput(true);
      setActionMsg('Please enter a GitHub token first');
      return null;
    }
    return token;
  };

  const changeIssueStatus = async (issue: Issue, newStatus: string) => {
    const t = requireToken();
    if (!t) return;
    setActionLoading(issue.number);
    setActionMsg(null);
    try {
      const currentStatus = getStatus(issue);
      const removeLabels = ['todo', 'in-progress', 'done', 'failed'].filter(l => l !== newStatus);

      // Remove old status labels
      for (const label of removeLabels) {
        if (hasLabel(issue, label)) {
          try {
            await ghApiCall(`/issues/${issue.number}/labels/${label}`, 'DELETE', t);
          } catch { /* label may not exist */ }
        }
      }

      // Add new status label (except 'done' which uses close)
      if (newStatus === 'done') {
        await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'closed' });
      } else {
        if (issue.state === 'closed') {
          await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'open' });
        }
        await ghApiCall(`/issues/${issue.number}/labels`, 'POST', t, { labels: [newStatus] });
      }

      setActionMsg(`#${issue.number} -> ${newStatus}`);
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setActionLoading(null);
  };

  const closeIssue = async (issue: Issue) => {
    const t = requireToken();
    if (!t) return;
    setActionLoading(issue.number);
    try {
      await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'closed' });
      setActionMsg(`Closed #${issue.number}`);
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setActionLoading(null);
  };

  const reopenIssue = async (issue: Issue) => {
    const t = requireToken();
    if (!t) return;
    setActionLoading(issue.number);
    try {
      await ghApiCall(`/issues/${issue.number}`, 'PATCH', t, { state: 'open' });
      await ghApiCall(`/issues/${issue.number}/labels`, 'POST', t, { labels: ['todo'] });
      setActionMsg(`Reopened #${issue.number}`);
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setActionLoading(null);
  };

  // ─── Batch issue creation ───────────────────────────────────────────────

  const existingClassNames = new Set(issues.map(i => extractClassName(i.title)));

  const toggleClass = (fqn: string) => {
    setSelectedClasses(prev => {
      const next = new Set(prev);
      if (next.has(fqn)) next.delete(fqn); else next.add(fqn);
      return next;
    });
  };

  const selectAllInTier = () => {
    const tier = TIER_CLASSES[createTier];
    if (!tier) return;
    const all = new Set(selectedClasses);
    tier.classes.forEach(c => {
      const fqn = `${c.pkg}.${c.cls}`;
      if (!existingClassNames.has(fqn)) all.add(fqn);
    });
    setSelectedClasses(all);
  };

  const deselectAll = () => setSelectedClasses(new Set());

  const createSelectedIssues = async () => {
    const t = requireToken();
    if (!t || selectedClasses.size === 0) return;
    setCreating(true);
    setCreateLog([]);
    const tier = TIER_CLASSES[createTier];

    for (const c of tier.classes) {
      const fqn = `${c.pkg}.${c.cls}`;
      if (!selectedClasses.has(fqn)) continue;

      const title = `[SHIM] Implement ${fqn}`;
      const relpath = `shim/java/${c.pkg.replace(/\./g, '/')}/${c.cls}.java`;
      const skill = PACKAGE_SKILL_MAP[c.pkg] || 'A2OH-JAVA-TO-ARKTS';
      const body = `## Class: \`${fqn}\`

**Tier:** ${createTier.toUpperCase()} ${tier.label.split(' — ')[1] || ''}
**Description:** ${c.desc}
**File:** \`${relpath}\`
**Skill:** \`skills/${skill}.md\`

## Task

Replace stub implementations (\`return null\`, \`return 0\`, \`return false\`) with **real Java logic**.
Read \`skills/${skill}.md\` for Android-to-OpenHarmony conversion rules.

## Verification

\`\`\`bash
cd test-apps && ./run-local-tests.sh headless
# Baseline: 497/502 pass. Must not regress.
\`\`\`
`;

      try {
        await ghApiCall('/issues', 'POST', t, {
          title,
          body,
          labels: [`tier-${createTier}`, 'todo', 'non-ui', 'shim'],
        });
        setCreateLog(prev => [...prev, `Created: ${fqn}`]);
      } catch (e: any) {
        setCreateLog(prev => [...prev, `FAILED ${fqn}: ${e.message}`]);
      }
    }

    setCreating(false);
    setSelectedClasses(new Set());
    refresh();
  };

  // ─── Single issue creation ──────────────────────────────────────────────

  const createSingleIssue = async () => {
    const t = requireToken();
    if (!t || !singleClassName.trim()) return;
    setSingleCreating(true);
    setActionMsg(null);

    const fqn = singleClassName.trim();
    const parts = fqn.split('.');
    const cls = parts[parts.length - 1];
    const pkg = parts.slice(0, -1).join('.');
    const relpath = `shim/java/${pkg.replace(/\./g, '/')}/${cls}.java`;
    const skill = PACKAGE_SKILL_MAP[pkg] || 'A2OH-JAVA-TO-ARKTS';
    const tierLabel = singleTier.toUpperCase();
    const tierDesc = { a: 'Pure Java', b: 'I/O with Java Fallback', c: 'System Services (needs OHBridge)', d: 'UI Components (needs ArkUI)' }[singleTier] || '';

    const title = `[SHIM] Implement ${fqn}`;
    const body = `## Class: \`${fqn}\`

**Tier:** ${tierLabel} (${tierDesc})
**Description:** ${singleDesc || cls}
**File:** \`${relpath}\`
**Skill:** \`skills/${skill}.md\`

## Task

Replace stub implementations (\`return null\`, \`return 0\`, \`return false\`) with **real Java logic**.

### Conversion Skill
Read \`skills/${skill}.md\` for Android-to-OpenHarmony conversion rules for this package.

Query the DB for API details:
\`\`\`bash
sqlite3 database/api_compat.db "SELECT a.name, a.signature, m.score, m.mapping_type, m.effort_level FROM api_mappings m JOIN android_apis a ON m.android_api_id = a.id JOIN android_types t ON a.type_id = t.id WHERE t.full_name = '${cls}' ORDER BY m.score DESC"
\`\`\`

## Verification

\`\`\`bash
cd test-apps && ./run-local-tests.sh headless
# Baseline: 497/502 pass. Must not regress.
\`\`\`
`;

    try {
      await ghApiCall('/issues', 'POST', t, {
        title,
        body,
        labels: [`tier-${singleTier}`, 'todo', 'non-ui', 'shim'],
      });
      setActionMsg(`Created: ${title}`);
      setSingleClassName('');
      setSingleDesc('');
      refresh();
    } catch (e: any) {
      setActionMsg(`Error: ${e.message}`);
    }
    setSingleCreating(false);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between flex-wrap gap-3">
        <h1 className="text-2xl font-bold">Shim Orchestrator</h1>
        <div className="flex items-center gap-3 flex-wrap">
          {/* Token management */}
          {!token ? (
            <button
              onClick={() => setShowTokenInput(true)}
              className="px-3 py-1.5 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600"
            >
              Set GitHub Token
            </button>
          ) : (
            <span className="text-xs text-green-500 flex items-center gap-1">
              <span className="w-1.5 h-1.5 bg-green-500 rounded-full" />
              Token set
              <button onClick={() => { setToken(''); localStorage.removeItem('gh_token'); }} className="text-gray-500 hover:text-red-400 ml-1">[clear]</button>
            </span>
          )}
          <label className="flex items-center gap-1.5 text-sm text-gray-400">
            <input type="checkbox" checked={autoRefresh} onChange={e => setAutoRefresh(e.target.checked)} className="rounded" />
            Auto-refresh
          </label>
          <button onClick={refresh} disabled={loading}
            className="px-3 py-1.5 bg-gray-800 border border-gray-700 rounded-lg text-sm hover:bg-gray-700 disabled:opacity-50">
            {loading ? 'Refreshing...' : 'Refresh'}
          </button>
          <span className="text-xs text-gray-600">Updated {timeSince(lastRefresh.toISOString())}</span>
        </div>
      </div>

      {/* Token input modal */}
      {showTokenInput && (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center" onClick={() => setShowTokenInput(false)}>
          <div className="bg-gray-900 border border-gray-700 rounded-xl p-6 max-w-md w-full mx-4" onClick={e => e.stopPropagation()}>
            <h3 className="text-lg font-semibold mb-3">GitHub Personal Access Token</h3>
            <p className="text-sm text-gray-400 mb-4">
              Required for issue management (create, close, label). Needs <code className="bg-gray-800 px-1 rounded">repo</code> scope.
              Stored in browser localStorage only.
            </p>
            <TokenInput onSave={saveToken} onCancel={() => setShowTokenInput(false)} />
          </div>
        </div>
      )}

      {/* Action messages */}
      {actionMsg && (
        <div className={`rounded-lg p-3 text-sm ${actionMsg.startsWith('Error') ? 'bg-red-900/30 border border-red-700/50 text-red-300' : 'bg-green-900/30 border border-green-700/50 text-green-300'}`}>
          {actionMsg}
          <button onClick={() => setActionMsg(null)} className="ml-3 text-gray-500 hover:text-white">[x]</button>
        </div>
      )}

      {error && (
        <div className="bg-red-900/30 border border-red-700/50 rounded-lg p-3 text-red-300 text-sm">{error}</div>
      )}

      {/* Progress bar */}
      <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
        <div className="flex items-center justify-between mb-3">
          <span className="text-lg font-semibold">Overall Progress</span>
          <span className="text-2xl font-bold text-green-400">{completionPct}%</span>
        </div>
        <div className="h-4 bg-gray-800 rounded-full overflow-hidden flex">
          {stats.done > 0 && <div className="h-full bg-green-500 transition-all duration-500" style={{ width: `${(stats.done / stats.total) * 100}%` }} title={`${stats.done} done`} />}
          {stats.inProgress > 0 && <div className="h-full bg-yellow-500 transition-all duration-500" style={{ width: `${(stats.inProgress / stats.total) * 100}%` }} title={`${stats.inProgress} in progress`} />}
          {stats.failed > 0 && <div className="h-full bg-red-500 transition-all duration-500" style={{ width: `${(stats.failed / stats.total) * 100}%` }} title={`${stats.failed} failed`} />}
        </div>
        <div className="flex gap-6 mt-3 text-sm flex-wrap">
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-green-500" /> {stats.done} done</span>
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-yellow-500 animate-pulse" /> {stats.inProgress} in progress</span>
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-gray-500" /> {stats.todo} todo</span>
          <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded-full bg-red-500" /> {stats.failed} failed</span>
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-3">
        <StatCard label="Total" value={stats.total} />
        <StatCard label="Completed" value={stats.done} color="text-green-400" />
        <StatCard label="Active" value={stats.inProgress} color="text-yellow-400" />
        <StatCard label="Failed" value={stats.failed} color="text-red-400" />
        <StatCard label="Tier A" value={stats.tierA} color="text-blue-400" />
        <StatCard label="Tier B" value={stats.tierB} color="text-yellow-400" />
        <StatCard label="Tier C" value={stats.tierC} color="text-orange-400" />
        <StatCard label="Tier D" value={stats.tierD} color="text-red-400" />
      </div>

      {/* Action bar: filters + create button */}
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex gap-2 flex-wrap">
          {/* Status filters */}
          {['all', 'todo', 'in-progress', 'done', 'failed'].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              className={`px-3 py-1.5 rounded-lg text-sm ${filter === f ? 'bg-blue-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'}`}>
              {f === 'all' ? `All (${stats.total})` : f === 'todo' ? `Todo (${stats.todo})` : f === 'in-progress' ? `Active (${stats.inProgress})` : f === 'done' ? `Done (${stats.done})` : `Failed (${stats.failed})`}
            </button>
          ))}
          <span className="text-gray-700">|</span>
          {/* Tier filters */}
          {['all', 'A', 'B', 'C', 'D'].map(t => (
            <button key={t} onClick={() => setTierFilter(t)}
              className={`px-3 py-1.5 rounded-lg text-sm ${tierFilter === t ? 'bg-purple-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'}`}>
              {t === 'all' ? 'All Tiers' : `Tier ${t}`}
            </button>
          ))}
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => { setShowSingleCreate(!showSingleCreate); setShowCreatePanel(false); }}
            className="px-4 py-2 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600 font-medium"
          >
            {showSingleCreate ? 'Close' : '+ Create Issue'}
          </button>
          <button
            onClick={() => { setShowCreatePanel(!showCreatePanel); setShowSingleCreate(false); }}
            className="px-4 py-2 bg-green-700 text-white rounded-lg text-sm hover:bg-green-600 font-medium"
          >
            {showCreatePanel ? 'Close' : '+ Batch Add'}
          </button>
        </div>
      </div>

      {/* Single issue create */}
      {showSingleCreate && (
        <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 space-y-4">
          <h2 className="text-lg font-semibold">Create Single Issue</h2>
          <p className="text-sm text-gray-400">
            Enter any Android class name. The skill file is auto-detected from the package.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
            <div className="md:col-span-2">
              <label className="block text-xs text-gray-500 mb-1">Class (fully qualified)</label>
              <input type="text" value={singleClassName} onChange={e => setSingleClassName(e.target.value)}
                placeholder="android.graphics.Bitmap"
                className="w-full px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500" />
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">Tier</label>
              <select value={singleTier} onChange={e => setSingleTier(e.target.value)}
                className="w-full px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200">
                <option value="a">A — Pure Java</option>
                <option value="b">B — I/O Fallback</option>
                <option value="c">C — System (OHBridge)</option>
                <option value="d">D — UI (ArkUI)</option>
              </select>
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">Description (optional)</label>
              <input type="text" value={singleDesc} onChange={e => setSingleDesc(e.target.value)}
                placeholder="Short description"
                className="w-full px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500" />
            </div>
          </div>
          {singleClassName && (
            <div className="text-xs text-gray-500">
              Skill: <code className="bg-gray-800 px-1.5 py-0.5 rounded text-gray-300">
                {PACKAGE_SKILL_MAP[singleClassName.split('.').slice(0, -1).join('.')] || 'A2OH-JAVA-TO-ARKTS'}
              </code>
              {' | '}File: <code className="bg-gray-800 px-1.5 py-0.5 rounded text-gray-300">
                shim/java/{singleClassName.replace(/\./g, '/').replace(/\/([^/]+)$/, '/$1')}.java
              </code>
              {existingClassNames.has(singleClassName) && (
                <span className="ml-2 text-yellow-400">Issue already exists</span>
              )}
            </div>
          )}
          <div className="flex justify-end">
            {!token ? (
              <button onClick={() => setShowTokenInput(true)}
                className="px-4 py-2 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600">Set Token First</button>
            ) : (
              <button onClick={createSingleIssue}
                disabled={!singleClassName.trim() || singleCreating || existingClassNames.has(singleClassName)}
                className="px-4 py-2 bg-green-700 text-white rounded-lg text-sm hover:bg-green-600 disabled:opacity-50 font-medium">
                {singleCreating ? 'Creating...' : 'Create Issue'}
              </button>
            )}
          </div>
        </div>
      )}

      {/* Batch create panel */}
      {showCreatePanel && (
        <BatchCreatePanel
          createTier={createTier}
          setCreateTier={setCreateTier}
          selectedClasses={selectedClasses}
          existingClassNames={existingClassNames}
          toggleClass={toggleClass}
          selectAllInTier={selectAllInTier}
          deselectAll={deselectAll}
          createSelectedIssues={createSelectedIssues}
          creating={creating}
          createLog={createLog}
          hasToken={!!token}
          onNeedToken={() => setShowTokenInput(true)}
        />
      )}

      {/* Issue table */}
      <div className="bg-gray-900 rounded-xl border border-gray-800 overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-gray-400 border-b border-gray-800 text-left">
              <th className="py-2 px-3 w-12">#</th>
              <th className="py-2 px-3">Class</th>
              <th className="py-2 px-3 w-16">Tier</th>
              <th className="py-2 px-3 w-28">Status</th>
              <th className="py-2 px-3 w-24">Updated</th>
              <th className="py-2 px-3 w-28">Worker</th>
              <th className="py-2 px-3 w-40 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(issue => {
              const status = getStatus(issue);
              const tier = getTier(issue);
              const isActioning = actionLoading === issue.number;
              return (
                <tr key={issue.number} className="border-b border-gray-800/50 hover:bg-gray-800/30 group">
                  <td className="py-2 px-3 text-gray-500 font-mono">{issue.number}</td>
                  <td className="py-2 px-3">
                    <a href={`https://github.com/${REPO}/issues/${issue.number}`} target="_blank" rel="noopener noreferrer"
                      className="text-blue-400 hover:underline">
                      {extractClassName(issue.title)}
                    </a>
                  </td>
                  <td className="py-2 px-3">
                    <span className={`text-xs px-2 py-0.5 rounded ${TIER_COLORS[tier]}`}>{tier}</span>
                  </td>
                  <td className="py-2 px-3">
                    <span className={`inline-flex items-center gap-1.5 text-xs px-2 py-0.5 rounded ${STATUS_COLORS[status]}`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${STATUS_DOT[status]}`} />
                      {status}
                    </span>
                  </td>
                  <td className="py-2 px-3 text-gray-500 text-xs">{timeSince(issue.updated_at)}</td>
                  <td className="py-2 px-3 text-gray-500 text-xs">
                    {issue.assignee?.login || (status === 'in-progress' ? 'claiming...' : '-')}
                  </td>
                  <td className="py-2 px-3 text-right">
                    {isActioning ? (
                      <span className="text-xs text-gray-500 animate-pulse">Working...</span>
                    ) : (
                      <div className="flex gap-1 justify-end opacity-0 group-hover:opacity-100 transition-opacity">
                        {status === 'todo' && (
                          <ActionBtn label="Claim" color="bg-yellow-700" onClick={() => changeIssueStatus(issue, 'in-progress')} />
                        )}
                        {status === 'in-progress' && (
                          <>
                            <ActionBtn label="Done" color="bg-green-700" onClick={() => closeIssue(issue)} />
                            <ActionBtn label="Fail" color="bg-red-700" onClick={() => changeIssueStatus(issue, 'failed')} />
                            <ActionBtn label="Release" color="bg-gray-600" onClick={() => changeIssueStatus(issue, 'todo')} />
                          </>
                        )}
                        {status === 'failed' && (
                          <>
                            <ActionBtn label="Retry" color="bg-yellow-700" onClick={() => changeIssueStatus(issue, 'todo')} />
                            <ActionBtn label="Close" color="bg-gray-600" onClick={() => closeIssue(issue)} />
                          </>
                        )}
                        {status === 'done' && (
                          <ActionBtn label="Reopen" color="bg-gray-600" onClick={() => reopenIssue(issue)} />
                        )}
                      </div>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="p-8 text-center text-gray-500">
            {loading ? 'Loading issues...' : 'No issues match filters'}
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

// ─── Sub-components ─────────────────────────────────────────────────────────

function StatCard({ label, value, color = 'text-white' }: { label: string; value: number; color?: string }) {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-3">
      <div className="text-xs text-gray-500 uppercase tracking-wide">{label}</div>
      <div className={`text-xl font-bold mt-1 ${color}`}>{value}</div>
    </div>
  );
}

function ActionBtn({ label, color, onClick }: { label: string; color: string; onClick: () => void }) {
  return (
    <button onClick={onClick} className={`px-2 py-0.5 ${color} text-white text-xs rounded hover:opacity-80`}>
      {label}
    </button>
  );
}

function TokenInput({ onSave, onCancel }: { onSave: (t: string) => void; onCancel: () => void }) {
  const [val, setVal] = useState('');
  return (
    <div className="flex gap-2">
      <input type="password" value={val} onChange={e => setVal(e.target.value)} placeholder="ghp_..."
        className="flex-1 px-3 py-2 bg-gray-800 border border-gray-700 rounded-lg text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500" />
      <button onClick={() => onSave(val)} disabled={!val}
        className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-500 disabled:opacity-50">Save</button>
      <button onClick={onCancel}
        className="px-4 py-2 bg-gray-700 text-gray-300 rounded-lg text-sm hover:bg-gray-600">Cancel</button>
    </div>
  );
}

function BatchCreatePanel({
  createTier, setCreateTier, selectedClasses, existingClassNames, toggleClass,
  selectAllInTier, deselectAll, createSelectedIssues, creating, createLog,
  hasToken, onNeedToken,
}: {
  createTier: string;
  setCreateTier: (t: string) => void;
  selectedClasses: Set<string>;
  existingClassNames: Set<string>;
  toggleClass: (fqn: string) => void;
  selectAllInTier: () => void;
  deselectAll: () => void;
  createSelectedIssues: () => void;
  creating: boolean;
  createLog: string[];
  hasToken: boolean;
  onNeedToken: () => void;
}) {
  const tier = TIER_CLASSES[createTier];

  return (
    <div className="bg-gray-900 rounded-xl border border-gray-800 p-5 space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Add Issues by Tier</h2>
        <div className="flex gap-2">
          {Object.entries(TIER_CLASSES).map(([key, val]) => (
            <button key={key} onClick={() => { setCreateTier(key); deselectAll(); }}
              className={`px-3 py-1.5 rounded-lg text-sm ${createTier === key ? 'bg-purple-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'}`}>
              {key.toUpperCase()}
            </button>
          ))}
        </div>
      </div>

      <div className="flex items-center justify-between text-sm">
        <span className={tier.color}>{tier.label} ({tier.classes.length} classes)</span>
        <div className="flex gap-2">
          <button onClick={selectAllInTier} className="text-xs text-blue-400 hover:text-blue-300">Select all new</button>
          <button onClick={deselectAll} className="text-xs text-gray-500 hover:text-gray-300">Deselect all</button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2 max-h-80 overflow-y-auto">
        {tier.classes.map(c => {
          const fqn = `${c.pkg}.${c.cls}`;
          const exists = existingClassNames.has(fqn);
          const selected = selectedClasses.has(fqn);
          return (
            <label key={fqn}
              className={`flex items-start gap-2 p-2 rounded-lg text-sm cursor-pointer ${exists ? 'opacity-40 cursor-not-allowed' : selected ? 'bg-blue-900/30 border border-blue-700/50' : 'bg-gray-800/50 hover:bg-gray-800'}`}>
              <input type="checkbox" checked={selected} disabled={exists}
                onChange={() => !exists && toggleClass(fqn)} className="mt-0.5 rounded" />
              <div>
                <div className={`font-mono text-xs ${exists ? 'text-gray-500 line-through' : 'text-gray-200'}`}>{fqn}</div>
                <div className="text-xs text-gray-500">{c.desc} {exists && '(exists)'}</div>
              </div>
            </label>
          );
        })}
      </div>

      <div className="flex items-center justify-between pt-2 border-t border-gray-800">
        <span className="text-sm text-gray-400">{selectedClasses.size} selected</span>
        {!hasToken ? (
          <button onClick={onNeedToken}
            className="px-4 py-2 bg-blue-700 text-white rounded-lg text-sm hover:bg-blue-600">
            Set Token to Create
          </button>
        ) : (
          <button onClick={createSelectedIssues} disabled={selectedClasses.size === 0 || creating}
            className="px-4 py-2 bg-green-700 text-white rounded-lg text-sm hover:bg-green-600 disabled:opacity-50 font-medium">
            {creating ? 'Creating...' : `Create ${selectedClasses.size} Issues`}
          </button>
        )}
      </div>

      {createLog.length > 0 && (
        <div className="bg-gray-800 rounded-lg p-3 max-h-40 overflow-y-auto">
          {createLog.map((log, i) => (
            <div key={i} className={`text-xs font-mono ${log.startsWith('FAILED') ? 'text-red-400' : 'text-green-400'}`}>{log}</div>
          ))}
        </div>
      )}
    </div>
  );
}
