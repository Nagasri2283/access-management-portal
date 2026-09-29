import { Link } from 'react-router-dom'

export default function Unauthorized() {
  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Access denied</h1>
        <p>Your account role doesn't have permission to view that page.</p>
        <Link to="/login">Back to login</Link>
      </div>
    </div>
  )
}
