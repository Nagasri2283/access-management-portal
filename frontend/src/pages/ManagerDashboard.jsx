import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import StatusBadge from '../components/StatusBadge'
import api from '../api/axios'

export default function ManagerDashboard() {
  const [requests, setRequests] = useState([])
  const [filter, setFilter] = useState('PENDING')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [commentDrafts, setCommentDrafts] = useState({})

  const loadRequests = async (status) => {
    setLoading(true)
    setError('')
    try {
      const params = status === 'ALL' ? {} : { status }
      const { data } = await api.get('/api/requests', { params })
      setRequests(data)
    } catch (err) {
      setError('Failed to load requests. Is the backend running?')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadRequests(filter)
  }, [filter])

  const handleReview = async (id, action) => {
    const comment = commentDrafts[id] || ''
    try {
      await api.put(`/api/requests/${id}/${action}`, { comment })
      loadRequests(filter)
    } catch (err) {
      setError(err.response?.data?.message || `Could not ${action} request.`)
    }
  }

  return (
    <div className="page">
      <Navbar />
      <div className="content">
        <h1>Review Requests</h1>

        <div className="filter-bar">
          {['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'ALL'].map((s) => (
            <button
              key={s}
              className={`chip ${filter === s ? 'chip-active' : ''}`}
              onClick={() => setFilter(s)}
            >
              {s}
            </button>
          ))}
        </div>

        {error && <div className="alert-error">{error}</div>}

        <div className="card">
          {loading ? (
            <p>Loading...</p>
          ) : requests.length === 0 ? (
            <p className="empty-state">Nothing here.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Requester</th>
                  <th>Resource</th>
                  <th>Justification</th>
                  <th>Status</th>
                  <th>Comment</th>
                  {filter === 'PENDING' && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {requests.map((r) => (
                  <tr key={r.id}>
                    <td>{r.requesterName}<div className="subtext">{r.requesterEmail}</div></td>
                    <td>{r.resourceName}</td>
                    <td>{r.justification}</td>
                    <td><StatusBadge status={r.status} /></td>
                    <td>
                      {r.status === 'PENDING' ? (
                        <input
                          className="comment-input"
                          placeholder="Optional comment"
                          value={commentDrafts[r.id] || ''}
                          onChange={(e) =>
                            setCommentDrafts((prev) => ({ ...prev, [r.id]: e.target.value }))
                          }
                        />
                      ) : (
                        r.reviewComment || '—'
                      )}
                    </td>
                    {filter === 'PENDING' && (
                      <td className="actions-cell">
                        <button className="btn-approve" onClick={() => handleReview(r.id, 'approve')}>Approve</button>
                        <button className="btn-reject" onClick={() => handleReview(r.id, 'reject')}>Reject</button>
                      </td>
                    )}
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
