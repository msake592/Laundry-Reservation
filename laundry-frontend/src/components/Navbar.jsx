import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const username = localStorage.getItem("username");

  const getHomePath = () => {
    if (!token) {
      return "/login";
    }

    if (role === "ADMIN") {
      return "/admin";
    }

    if (role === "USER") {
      return "/user";
    }

    return "/login";
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("username");
    navigate("/login");
  };

  return (
    <nav>
      <Link to={getHomePath()}>Dorm Laundry</Link>

      <div>
        {!token && (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}

        {token && role === "ADMIN" && <Link to="/admin">Admin Dashboard</Link>}
        {token && role === "USER" && <Link to="/user">User Dashboard</Link>}

        {token && (
          <>
            <span>{username}</span>
            <button type="button" onClick={handleLogout}>
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;