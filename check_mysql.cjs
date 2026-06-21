require('dotenv').config();
const mysql = require('mysql2/promise');

async function check() {
  const connection = await mysql.createConnection({
    host: process.env.MYSQL_HOST || 'localhost',
    port: parseInt(process.env.MYSQL_PORT) || 3306,
    user: process.env.MYSQL_USER || 'root',
    password: process.env.MYSQL_PASSWORD || '',
    database: process.env.MYSQL_DATABASE || 'connectit_db'
  });
  
  const [rows] = await connection.query('SHOW TABLES');
  console.log('Tables in MySQL:', rows.map(r => Object.values(r)[0]));
  await connection.end();
}

check().catch(console.error);
