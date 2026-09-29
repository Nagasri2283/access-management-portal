import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ShieldCheck, LogOut } from 'lucide-react'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <ShieldCheck size={22} />
        <span>Access Portal</span>
      </div>
      {user && (
        <div className="navbar-user">
          <span className="navbar-name">{user.fullName}</span>
          <span className={`badge role-${user.role.toLowerCase()}`}>{user.role}</span>
          <button className="btn-ghost" onClick={handleLogout}>
            <LogOut size={16} />
            Logout
          </button>
        </div>
      )}
    </nav>
  )
}
