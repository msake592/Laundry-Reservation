import { useEffect, useState } from "react";
import { getMachines } from "../services/machineService";
import {
  createReservation,
  deleteReservation,
  getMyReservations,
  getReservations,
} from "../services/reservationService";

function UserDashboard() {
  const username = localStorage.getItem("username");

  const [machines, setMachines] = useState([]);
  const [myReservations, setMyReservations] = useState([]);
  const [allReservations, setAllReservations] = useState([]);
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
    const data = err.response?.data;

    if (typeof data === "string") {
      return data;
    }

    if (data?.message) {
      return data.message;
    }

    if (data?.errors) {
      return JSON.stringify(data.errors);
    }

    if (data) {
      return JSON.stringify(data);
    }

    return err.message || "Something went wrong.";
  };

  const loadMachines = async () => {
    const data = await getMachines();

    const availableMachines = data.filter(
      (machine) => machine.status === "AVAILABLE"
    );

    setMachines(availableMachines);

    if (availableMachines.length > 0 && !selectedMachineId) {
      setSelectedMachineId(String(availableMachines[0].id));
    }
  };

  const loadMyReservations = async () => {
    const data = await getMyReservations();
    setMyReservations(data);
  };

  const loadAllReservations = async () => {
    const data = await getReservations();
    setAllReservations(data);
  };

  const loadDashboardData = async () => {
    setError("");

    try {
      await loadMachines();
      await loadMyReservations();
      await loadAllReservations();
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
      await loadMyReservations();
      await loadAllReservations();
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
      await loadMyReservations();
      await loadAllReservations();
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
        <h1>User Dashboard</h1>
        <button onClick={handleLogout}>Logout</button>
      </div>

      <p>Welcome, {username}</p>

      {message && <p className="success-message">{message}</p>}
      {error && <p className="error-message">{error}</p>}

      <div className="dashboard-card">
        <h2>Create Reservation</h2>

        <form onSubmit={handleCreateReservation}>
          <select
            value={selectedMachineId}
            onChange={(e) => setSelectedMachineId(e.target.value)}
          >
            {machines.map((machine) => (
              <option key={machine.id} value={machine.id}>
                #{machine.id} - {machine.type}
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
            Create Reservation
          </button>
        </form>
      </div>

      <div className="dashboard-card">
        <h2>Available Machines</h2>

        {machines.length === 0 ? (
          <p>No available machines found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {machines.map((machine) => (
                <tr key={machine.id}>
                  <td>{machine.id}</td>
                  <td>{machine.type}</td>
                  <td>{machine.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="dashboard-card">
        <h2>My Reservations</h2>

        {myReservations.length === 0 ? (
          <p>No reservations found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Machine</th>
                <th>Start Time</th>
                <th>End Time</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {myReservations.map((reservation) => (
                <tr key={reservation.id}>
                  <td>{reservation.id}</td>
                  <td>
                    {reservation.machine?.id || reservation.machineId || "-"}
                  </td>
                  <td>{reservation.startTime}</td>
                  <td>{reservation.endTime}</td>
                  <td>{reservation.status}</td>
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

      <div className="dashboard-card">
        <h2>All Reservations</h2>

        {allReservations.length === 0 ? (
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
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {allReservations.map((reservation) => (
                <tr key={reservation.id}>
                  <td>{reservation.id}</td>
                  <td>
                    {reservation.machine?.id || reservation.machineId || "-"}
                  </td>
                  <td>{reservation.userId || reservation.user?.username || "-"}</td>
                  <td>{reservation.startTime}</td>
                  <td>{reservation.endTime}</td>
                  <td>{reservation.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default UserDashboard;