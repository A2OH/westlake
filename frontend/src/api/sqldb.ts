import initSqlJs from 'sql.js';
import type { Database } from 'sql.js';

let db: Database | null = null;
let dbPromise: Promise<Database> | null = null;
let _loadProgress = 0;
const _listeners: ((pct: number) => void)[] = [];

export function onLoadProgress(fn: (pct: number) => void) {
  _listeners.push(fn);
  if (_loadProgress > 0) fn(_loadProgress);
  return () => { const i = _listeners.indexOf(fn); if (i >= 0) _listeners.splice(i, 1); };
}

function setProgress(pct: number) {
  _loadProgress = pct;
  _listeners.forEach(fn => fn(pct));
}

export async function getDb(): Promise<Database> {
  if (db) return db;
  if (dbPromise) return dbPromise;

  dbPromise = (async () => {
    setProgress(5);
    const SQL = await initSqlJs({
      locateFile: (file: string) => `https://sql.js.org/dist/${file}`,
    });
    setProgress(15);
    const base = import.meta.env.BASE_URL || '/';
    const resp = await fetch(`${base}api_compat.db`);
    const total = Number(resp.headers.get('content-length')) || 37_000_000;
    const reader = resp.body?.getReader();

    if (reader) {
      const chunks: Uint8Array[] = [];
      let received = 0;
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        chunks.push(value);
        received += value.length;
        setProgress(15 + Math.round((received / total) * 80));
      }
      const buf = new Uint8Array(received);
      let offset = 0;
      for (const chunk of chunks) {
        buf.set(chunk, offset);
        offset += chunk.length;
      }
      db = new SQL.Database(buf);
    } else {
      const buf = await resp.arrayBuffer();
      db = new SQL.Database(new Uint8Array(buf));
    }

    setProgress(100);
    return db;
  })();

  return dbPromise;
}

export function queryAll(db: Database, sql: string, params: any[] = []): any[] {
  const stmt = db.prepare(sql);
  stmt.bind(params);
  const rows: any[] = [];
  while (stmt.step()) {
    rows.push(stmt.getAsObject());
  }
  stmt.free();
  return rows;
}

export function queryOne(db: Database, sql: string, params: any[] = []): any | null {
  const rows = queryAll(db, sql, params);
  return rows.length > 0 ? rows[0] : null;
}
