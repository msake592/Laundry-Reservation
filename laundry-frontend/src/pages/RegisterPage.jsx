import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../services/authService";

function RegisterPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
    role: "USER",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

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
    setSuccess("");

    try {
      await register(formData);
      setSuccess("Registration successful. You can now login.");

      setTimeout(() => {
        navigate("/");
      }, 1000);
    } catch (err) {
      setError("Registration failed.");
    }
  };

  return (
    <div className="page-container">
      <div className="auth-card">
        <h1>Laundry Management</h1>
        <h2>Register</h2>

        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}

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

          <div className="form-group">
            <label>Role</label>
            <select name="role" value={formData.role} onChange={handleChange}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          <button type="submit">Register</button>
        </form>

        <p>
          Already have an account? <Link to="/">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;