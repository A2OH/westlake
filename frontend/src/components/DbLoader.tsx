import { useEffect, useState } from 'react';
import { onStatusChange, getDb } from '../api/sqldb';
import type { DbStatus } from '../api/sqldb';

const STATUS_TEXT: Record<DbStatus, string> = {
  init: 'Initializing...',
  wasm: 'Loading SQL engine...',
  downloading: 'Downloading database (~10MB compressed)...',
  ready: '',
  error: 'Failed to load database. Please refresh.',
};

export default function DbLoader({ children }: { children: React.ReactNode }) {
  const [ready, setReady] = useState(false);
  const [status, setStatus] = useState<DbStatus>('init');
  const [isStatic, setIsStatic] = useState(false);

  useEffect(() => {
    const host = window.location.hostname;
    const needsDb = host.endsWith('.github.io') || host === 'harmony.moxin.app';
    if (!needsDb) {
      fetch('/api/health')
        .then(r => { if (r.ok) setReady(true); else startDb(); })
        .catch(() => startDb());
      return;
    }
    startDb();

    function startDb() {
      setIsStatic(true);
      const unsub = onStatusChange(setStatus);
      getDb().then(() => setReady(true));
      return unsub;
    }
  }, []);

  if (ready) return <>{children}</>;

  if (!isStatic) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center">
        <div className="text-gray-500">Connecting...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-black flex items-center justify-center">
      <div className="text-center space-y-4">
        <h1 className="text-xl font-bold text-white">Android → OpenHarmony Migration Portal</h1>
        <p className="text-sm text-gray-400">{STATUS_TEXT[status]}</p>
        {status !== 'error' && (
          <div className="w-64 h-1.5 bg-gray-800 rounded-full overflow-hidden mx-auto">
            <div className="h-full bg-blue-500 rounded-full animate-pulse" style={{ width: '100%' }} />
          </div>
        )}
        {status === 'error' && (
          <button
            onClick={() => window.location.reload()}
            className="px-4 py-2 bg-blue-600 rounded text-sm hover:bg-blue-500"
          >
            Retry
          </button>
        )}
      </div>
    </div>
  );
}
