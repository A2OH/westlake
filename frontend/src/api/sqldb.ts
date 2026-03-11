import initSqlJs from 'sql.js';
import type { Database } from 'sql.js';

let db: Database | null = null;
let dbPromise: Promise<Database> | null = null;

export type DbStatus = 'init' | 'wasm' | 'downloading' | 'ready' | 'error';
let _status: DbStatus = 'init';
const _listeners: ((status: DbStatus) => void)[] = [];

export function onStatusChange(fn: (status: DbStatus) => void) {
  _listeners.push(fn);
  fn(_status);
  return () => { const i = _listeners.indexOf(fn); if (i >= 0) _listeners.splice(i, 1); };
}

function setStatus(s: DbStatus) {
  _status = s;
  _listeners.forEach(fn => fn(s));
}

export async function getDb(): Promise<Database> {
  if (db) return db;
  if (dbPromise) return dbPromise;

  dbPromise = (async () => {
    try {
      setStatus('wasm');
      const base = import.meta.env.BASE_URL || '/';
      const SQL = await initSqlJs({
        locateFile: (file: string) => `${base}${file}`,
      });
      setStatus('downloading');
      const resp = await fetch(`${base}api_compat.db`);
      const buf = await resp.arrayBuffer();
      db = new SQL.Database(new Uint8Array(buf));
      setStatus('ready');
      return db;
    } catch (e) {
      setStatus('error');
      throw e;
    }
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
