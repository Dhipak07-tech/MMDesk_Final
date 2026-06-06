const mysql = require('mysql2/promise');
require('dotenv').config();

async function run() {
  console.log("=== Database Health Check ===");
  console.log(`Target: ${process.env.MYSQL_USER}@${process.env.MYSQL_HOST}:${process.env.MYSQL_PORT}/${process.env.MYSQL_DATABASE}`);

  let connection;
  try {
    connection = await mysql.createConnection({
      host: process.env.MYSQL_HOST || '127.0.0.1',
      port: parseInt(process.env.MYSQL_PORT || '3308'),
      user: process.env.MYSQL_USER || 'root',
      password: process.env.MYSQL_PASSWORD || '',
      database: process.env.MYSQL_DATABASE || 'connectit_db'
    });
    console.log("✓ Connection: SUCCESSFUL");
  } catch (err) {
    console.error("✗ Connection: FAILED");
    console.error(err.message);
    process.exit(1);
  }

  const checkTable = async (name) => {
    try {
      const [rows] = await connection.query(`SELECT COUNT(*) as count FROM \`${name}\``);
      console.log(`✓ Table ${name.padEnd(25)}: EXISTS (Count: ${rows[0].count})`);
      return true;
    } catch (err) {
      console.error(`✗ Table ${name.padEnd(25)}: ERROR (${err.message})`);
      return false;
    }
  };

  const tables = [
    'users',
    'tickets',
    'timesheets',
    'time_cards',
    'activity_sessions',
    'activity_entries',
    'sla_breaches',
    'notifications',
    'company_email_configs',
    'message_history',
    'ticket_activities',
    'email_queue',
    'email_logs',
    'email_threads',
    'notifications_queue',
    'system_settings',
    'meetings',
    'meeting_versions',
    'meeting_audit_logs',
    'ts_meetings',
    'ts_meeting_chat',
    'ts_meeting_attendance'
  ];

  let failed = false;
  for (const table of tables) {
    const ok = await checkTable(table);
    if (!ok) failed = true;
  }

  await connection.end();
  
  if (failed) {
    console.log("\n✗ Health Check result: FAILED (Some tables/columns are missing)");
    process.exit(1);
  } else {
    console.log("\n✓ Health Check result: ALL PASSED (Database is fully standardised on MySQL)");
  }
}

run().catch(console.error);
