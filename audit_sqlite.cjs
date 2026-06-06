const sqlite3 = require('sqlite3');
const { open } = require('sqlite');

(async () => {
  try {
    const db = await open({ filename: './timesheet.sqlite', driver: sqlite3.Database });
    const tables = await db.all("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name");
    console.log('SQLite tables found:', tables.length);
    for (const t of tables) {
      const count = await db.get('SELECT COUNT(*) as cnt FROM ' + t.name);
      console.log('  Table:', t.name, '-> Rows:', count.cnt);
    }
    await db.close();
    console.log('SQLite audit complete.');
  } catch (err) {
    console.error('SQLite audit error:', err.message);
  }
})();
