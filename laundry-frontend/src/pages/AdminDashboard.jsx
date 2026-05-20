/*function AdminDashboard() {
    const username = localStorage.getItem("username");
  
    const handleLogout = () => {
      localStorage.clear();
      window.location.href = "/";
    };
  
    return (
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h1>Admin Dashboard</h1>
          <button onClick={handleLogout}>Logout</button>
        </div>
  
        <p>Welcome, {username}</p>
  
        <div className="dashboard-card">
          <h2>Machine Management</h2>
          <p>Machine create/update features will be added here.</p>
        </div>
  
        <div className="dashboard-card">
          <h2>All Reservations</h2>
          <p>Reservation management features will be added here.</p>
        </div>
      </div>
    );
  }
  
  export default AdminDashboard;*/

  import { useEffect, useState } from "react";
import {
  createMachine,
  deleteMachine,
  getMachines,
  markMachineAsAvailable,
  markMachineAsBroken,
} from "../services/machineService";
import {
  createReservation,
  deleteReservation,
  getReservations,
} from "../services/reservationService";

function AdminDashboard() {
  const username = localStorage.getItem("username");

  const [machines, setMachines] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [machineType, setMachineType] = useState("WASHING");
  const [selectedMachineId, setSelectedMachineId] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/";
  };

  const getErrorMessage = (err) => {
    return (
      err.response?.data?.message ||
      err.response?.data ||
      err.message ||
      "Something went wrong."
    );
  };

  const loadMachines = async () => {
    const data = await getMachines();
    setMachines(data);

    if (data.length > 0 && !selectedMachineId) {
      setSelectedMachineId(String(data[0].id));
    }
  };

  const loadReservations = async () => {
    const data = await getReservations();
    setReservations(data);
  };

  const loadDashboardData = async () => {
    setError("");

    try {
      await loadMachines();
      await loadReservations();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleCreateMachine = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await createMachine(machineType);
      setMessage("Machine created successfully.");
      await loadMachines();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleDeleteMachine = async (machineId) => {
    setMessage("");
    setError("");

    try {
      await deleteMachine(machineId);
      setMessage("Machine deleted successfully.");
      await loadMachines();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleMarkBroken = async (machineId) => {
    setMessage("");
    setError("");

    try {
      await markMachineAsBroken(machineId);
      setMessage("Machine marked as broken.");
      await loadMachines();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleMarkAvailable = async (machineId) => {
    setMessage("");
    setError("");

    try {
      await markMachineAsAvailable(machineId);
      setMessage("Machine marked as available.");
      await loadMachines();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleCreateReservation = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await createReservation(selectedMachineId, startTime, endTime);
      setMessage("Reservation created successfully.");
      setStartTime("");
      setEndTime("");
      await loadReservations();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const handleDeleteReservation = async (reservationId) => {
    setMessage("");
    setError("");

    try {
      await deleteReservation(reservationId);
      setMessage("Reservation deleted successfully.");
      await loadReservations();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <button onClick={handleLogout}>Logout</button>
      </div>

      <p>Welcome, {username}</p>

      {message && <p className="success-message">{message}</p>}
      {error && <p className="error-message">{error}</p>}

      <div className="dashboard-card">
        <h2>Add Machine</h2>

        <form onSubmit={handleCreateMachine}>
          <select
            value={machineType}
            onChange={(e) => setMachineType(e.target.value)}
          >
            <option value="WASHING_MACHINE">Washing Machine</option>
            <option value="DRYER">Dryer</option>
          </select>

          <button type="submit">Add Machine</button>
        </form>
      </div>

      <div className="dashboard-card">
        <h2>Machines</h2>

        {machines.length === 0 ? (
          <p>No machines found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {machines.map((machine) => (
                <tr key={machine.id}>
                  <td>{machine.id}</td>
                  <td>{machine.type}</td>
                  <td>{machine.status}</td>
                  <td>
                    <button onClick={() => handleMarkBroken(machine.id)}>
                      Broken
                    </button>
                    <button onClick={() => handleMarkAvailable(machine.id)}>
                      Active
                    </button>
                    <button onClick={() => handleDeleteMachine(machine.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="dashboard-card">
        <h2>Add Reservation</h2>

        <form onSubmit={handleCreateReservation}>
          <select
            value={selectedMachineId}
            onChange={(e) => setSelectedMachineId(e.target.value)}
          >
            {machines.map((machine) => (
              <option key={machine.id} value={machine.id}>
                #{machine.id} - {machine.type} - {machine.status}
              </option>
            ))}
          </select>

          <input
            type="datetime-local"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
          />

          <input
            type="datetime-local"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
          />

          <button type="submit" disabled={!selectedMachineId}>
            Add Reservation
          </button>
        </form>
      </div>

      <div className="dashboard-card">
        <h2>Reservations</h2>

        {reservations.length === 0 ? (
          <p>No reservations found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Machine</th>
                <th>User</th>
                <th>Start Time</th>
                <th>End Time</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {reservations.map((reservation) => (
                <tr key={reservation.id}>
                  <td>{reservation.id}</td>
                  <td>
                    {reservation.machine?.id || reservation.machineId || "-"}
                  </td>
                  <td>{reservation.user?.username || reservation.userId || "-"}</td>
                  <td>{reservation.startTime}</td>
                  <td>{reservation.endTime}</td>
                  <td>
                    <button
                      onClick={() => handleDeleteReservation(reservation.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;