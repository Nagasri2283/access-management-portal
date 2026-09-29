import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import StatusBadge from '../components/StatusBadge'
import api from '../api/axios'

export default function AdminDashboard() {
  const [resources, setResources] = useState([])
  const [requests, setRequests] = useState([])
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [resourceType, setResourceType] = useState('')
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const loadAll = async () => {
    try {
      const [resourcesRes, requestsRes] = await Promise.all([
        api.get('/api/resources'),
        api.get('/api/requests', { params: {} }),
      ])
      setResources(resourcesRes.data)
      setRequests(requestsRes.data)
    } catch (err) {
      setError('Failed to load data. Is the backend running?')
    }
  }

  useEffect(() => {
    loadAll()
  }, [])

  const handleCreateResource = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')
    try {
      await api.post('/api/admin/resources', { name, description, resourceType })
      setMessage('Resource created.')
      setName('')
      setDescription('')
      setResourceType('')
      loadAll()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create resource.')
    }
  }

  const handleDeleteResource = async (id) => {
    try {
      await api.delete(`/api/admin/resources/${id}`)
      loadAll()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not delete resource.')
    }
  }

  return (
    <div className="page">
      <Navbar />
      <div className="content">
        <h1>Admin Console</h1>

        <div className="card">
          <h2>Add a resource</h2>
          <form className="inline-form" onSubmit={handleCreateResource}>
            <input placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} required />
            <input placeholder="Type (e.g. Database)" value={resourceType} onChange={(e) => setResourceType(e.target.value)} required />
            <input placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} required />
            <button className="btn-primary" type="submit">Add resource</button>
          </form>
          {message && <div className="alert-success">{message}</div>}
          {error && <div className="alert-error">{error}</div>}
        </div>

        <div className="card">
          <h2>Resource catalog</h2>
          <table className="data-table">
            <thead>
              <tr><th>Name</th><th>Type</th><th>Description</th><th></th></tr>
            </thead>
            <tbody>
              {resources.map((r) => (
                <tr key={r.id}>
                  <td>{r.name}</td>
                  <td>{r.resourceType}</td>
                  <td>{r.description}</td>
                  <td><button className="btn-ghost-small" onClick={() => handleDeleteResource(r.id)}>Delete</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="card">
          <h2>All access requests</h2>
          <table className="data-table">
            <thead>
              <tr><th>Requester</th><th>Resource</th><th>Status</th><th>Reviewed by</th></tr>
            </thead>
            <tbody>
              {requests.map((r) => (
                <tr key={r.id}>
                  <td>{r.requesterName}</td>
                  <td>{r.resourceName}</td>
                  <td><StatusBadge status={r.status} /></td>
                  <td>{r.reviewedByName || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
