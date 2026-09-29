import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import StatusBadge from '../components/StatusBadge'
import api from '../api/axios'

export default function EmployeeDashboard() {
  const [resources, setResources] = useState([])
  const [myRequests, setMyRequests] = useState([])
  const [resourceId, setResourceId] = useState('')
  const [justification, setJustification] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const loadData = async () => {
    setLoading(true)
    try {
      const [resourcesRes, requestsRes] = await Promise.all([
        api.get('/api/resources'),
        api.get('/api/requests/my'),
      ])
      setResources(resourcesRes.data)
      setMyRequests(requestsRes.data)
    } catch (err) {
      setError('Failed to load data. Is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')
    if (!resourceId || !justification.trim()) return

    try {
      await api.post('/api/requests', { resourceId: Number(resourceId), justification })
      setMessage('Request submitted successfully.')
      setJustification('')
      setResourceId('')
      loadData()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not submit request.')
    }
  }

  const handleCancel = async (id) => {
    try {
      await api.put(`/api/requests/${id}/cancel`)
      loadData()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not cancel request.')
    }
  }

  return (
    <div className="page">
      <Navbar />
      <div className="content">
        <h1>My Access Requests</h1>

        <div className="card">
          <h2>New request</h2>
          <form className="inline-form" onSubmit={handleSubmit}>
            <select value={resourceId} onChange={(e) => setResourceId(e.target.value)} required>
              <option value="">Select a resource...</option>
              {resources.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.name} ({r.resourceType})
                </option>
              ))}
            </select>
            <input
              placeholder="Why do you need access?"
              value={justification}
              onChange={(e) => setJustification(e.target.value)}
              required
            />
            <button className="btn-primary" type="submit">Submit request</button>
          </form>
          {message && <div className="alert-success">{message}</div>}
          {error && <div className="alert-error">{error}</div>}
        </div>

        <div className="card">
          <h2>History</h2>
          {loading ? (
            <p>Loading...</p>
          ) : myRequests.length === 0 ? (
            <p className="empty-state">No requests yet — submit one above.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Resource</th>
                  <th>Justification</th>
                  <th>Status</th>
                  <th>Reviewed by</th>
                  <th>Comment</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {myRequests.map((r) => (
                  <tr key={r.id}>
                    <td>{r.resourceName}</td>
                    <td>{r.justification}</td>
                    <td><StatusBadge status={r.status} /></td>
                    <td>{r.reviewedByName || '—'}</td>
                    <td>{r.reviewComment || '—'}</td>
                    <td>
                      {r.status === 'PENDING' && (
                        <button className="btn-ghost-small" onClick={() => handleCancel(r.id)}>Cancel</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  )
}
