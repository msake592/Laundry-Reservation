import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../services/authService";

function LoginPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    try {
      const data = await login(formData);

      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("username", data.username || formData.username);

      if (data.role === "ADMIN") {
        navigate("/admin");
      } else if (data.role === "USER") {
        navigate("/user");
      } else {
        setError("Unknown user role.");
      }
    } catch (err) {
      setError("Invalid username or password.");
    }
  };

  return (
    <div className="page-container">
      <div className="auth-card">
        <h1>Laundry Management</h1>
        <h2>Login</h2>

        {error && <p className="error-message">{error}</p>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          <button type="submit">Login</button>
        </form>

        <p>
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;