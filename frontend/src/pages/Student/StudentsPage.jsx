import { useEffect, useState } from 'react';
import Layout from '../../layouts/Layout';
import api from '../../api/axios';

export default function StudentsPage() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    api.get('/api/students')
      .then(res => setStudents(res.data?.data ?? []))
      .catch(() => setStudents([]))
      .finally(() => setLoading(false));
  }, []);

  const filtered = students.filter(s =>
    s.name?.toLowerCase().includes(search.toLowerCase()) ||
    s.email?.toLowerCase().includes(search.toLowerCase()) ||
    s.rollNumber?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Layout>
      <header className="dash-header">
        <div>
          <h1>Students</h1>
          <p>Manage student records and enrollment</p>
        </div>
        <button className="btn-primary">+ Add Student</button>
      </header>

      <div className="page-card">
        <div className="page-card-header">
          <h2>All Students <span className="count-badge">{students.length}</span></h2>
          <input
            type="text"
            placeholder="Search by name, email, or roll no..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="search-input"
          />
        </div>
        {loading ? (
          <div className="table-loading">Loading students...</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <p className="empty-icon">🎓</p>
            <p className="empty-title">{search ? 'No matching students' : 'No students yet'}</p>
            <p className="empty-sub">{search ? 'Try a different search term' : 'Add your first student to get started'}</p>
          </div>
        ) : (
          <table className="data-table">
            <thead>
              <tr><th>Name</th><th>Email</th><th>Roll No.</th><th>Department</th><th>Status</th></tr>
            </thead>
            <tbody>
              {filtered.map(s => (
                <tr key={s.id}>
                  <td>{s.name}</td>
                  <td>{s.email}</td>
                  <td>{s.rollNumber}</td>
                  <td>{s.departmentName}</td>
                  <td><span className={`badge ${s.active ? 'badge-active' : 'badge-inactive'}`}>{s.active ? 'Active' : 'Inactive'}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </Layout>
  );
}
