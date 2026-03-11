import { useEffect, useState } from 'react';
import { onLoadProgress, getDb } from '../api/sqldb';

export default function DbLoader({ children }: { children: React.ReactNode }) {
  const [ready, setReady] = useState(false);
  const [progress, setProgress] = useState(0);
  const [isStatic, setIsStatic] = useState(false);

  useEffect(() => {
    const host = window.location.hostname;
    const needsDb = host.endsWith('.github.io') || host === 'harmony.moxin.app';
    if (!needsDb) {
      // Backend mode — check if backend is available
      fetch('/api/health', { signal: AbortSignal.timeout?.(1500) })
        .then(r => { if (r.ok) setReady(true); else startDb(); })
        .catch(() => startDb());
      return;
    }
    startDb();

    function startDb() {
      setIsStatic(true);
      const unsub = onLoadProgress(setProgress);
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
        <p className="text-sm text-gray-400">Loading database ({Math.round(36 * progress / 100)}MB / 36MB)...</p>
        <div className="w-64 h-2 bg-gray-800 rounded-full overflow-hidden mx-auto">
          <div
            className="h-full bg-blue-500 rounded-full transition-all duration-300"
            style={{ width: `${progress}%` }}
          />
        </div>
        <p className="text-xs text-gray-600">{progress}%</p>
      </div>
    </div>
  );
}
