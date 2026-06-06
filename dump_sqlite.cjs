const sqlite3 = require('sqlite3');
const { open } = require('sqlite');

(async () => {
  try {
    const db = await open({ filename: './timesheet.sqlite', driver: sqlite3.Database });
    
    // Dump key tables
    const users = await db.all("SELECT id, uid, name, email, role, phone, is_active, created_at FROM users");
    console.log('\n=== USERS ===');
    console.log(JSON.stringify(users, null, 2));

    const tickets = await db.all("SELECT id, ticket_number, title, status, priority, assigned_to, created_by, created_at FROM tickets");
    console.log('\n=== TICKETS ===');
    console.log(JSON.stringify(tickets, null, 2));

    const timesheets = await db.all("SELECT id, user_id, week_start, week_end, status, total_hours FROM timesheets");
    console.log('\n=== TIMESHEETS ===');
    console.log(JSON.stringify(timesheets, null, 2));

    const timecards = await db.all("SELECT id, timesheet_id, user_id, entry_date, task, hours_worked, status FROM time_cards");
    console.log('\n=== TIME_CARDS ===');
    console.log(JSON.stringify(timecards, null, 2));

    const breaches = await db.all("SELECT id, record_id, record_type, assigned_user, sla_name, breach_duration, status FROM sla_breaches LIMIT 5");
    console.log('\n=== SLA_BREACHES (sample) ===');
    console.log(JSON.stringify(breaches, null, 2));

    const emailCfg = await db.all("SELECT id, company_name, email_address, smtp_host, smtp_port, imap_host, imap_port, is_active FROM company_email_configs");
    console.log('\n=== COMPANY_EMAIL_CONFIGS ===');
    console.log(JSON.stringify(emailCfg, null, 2));

    const sessions = await db.all("SELECT id, session_id, user_id, user_name, start_time, stop_time, duration, status FROM activity_sessions");
    console.log('\n=== ACTIVITY_SESSIONS ===');
    console.log(JSON.stringify(sessions, null, 2));

    await db.close();
    console.log('\nData dump complete.');
  } catch (err) {
    console.error('Error:', err.message);
  }
})();
