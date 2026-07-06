import { useEffect, useState } from 'react';
import Layout from '../../layouts/Layout';
import api from '../../api/axios';

export default function AttendancePage() {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/api/sessions')
      .then(res => setSessions(res.data?.data ?? []))
      .catch(() => setSessions([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout>
      <header className="dash-header">
        <div>
          <h1>Attendance</h1>
          <p>Manage and monitor attendance sessions</p>
        </div>
      </header>

      <div className="page-card">
        <div className="page-card-header">
          <h2>Active Sessions</h2>
          <button className="btn-primary">+ New Session</button>
        </div>
        {loading ? (
          <div className="table-loading">Loading sessions...</div>
        ) : sessions.length === 0 ? (
          <div className="empty-state">
            <p className="empty-icon">📋</p>
            <p className="empty-title">No sessions found</p>
            <p className="empty-sub">Create a new attendance session to get started</p>
          </div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Subject</th><th>Faculty</th><th>Date</th><th>Status</th><th>Present</th>
              </tr>
            </thead>
            <tbody>
              {sessions.map(s => (
                <tr key={s.id}>
                  <td>{s.subjectName}</td>
                  <td>{s.facultyName}</td>
                  <td>{s.sessionDate}</td>
                  <td><span className={`badge badge-${s.status?.toLowerCase()}`}>{s.status}</span></td>
                  <td>{s.presentCount}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </Layout>
  );
}
