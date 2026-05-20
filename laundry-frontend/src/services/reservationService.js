
/*
import api from "./api";

export const getAllReservations = async () => {
  const response = await api.get("/reservations");
  return response.data;
};

export const getReservationById = async (reservationId) => {
  const response = await api.get(`/reservations/${reservationId}`);
  return response.data;
};

export const createReservation = async (reservationData) => {
  const response = await api.post("/reservations", reservationData);
  return response.data;
};

export const deleteReservation = async (reservationId) => {
  const response = await api.delete(`/reservations/${reservationId}`);
  return response.data;
};

export const rescheduleReservation = async (reservationId, reservationData) => {
  const response = await api.put(`/reservations/${reservationId}`, reservationData);
  return response.data;
};*/

import api from "./api";

export const getReservations = async () => {
  const response = await api.get("/reservations");
  return response.data;
};

export const getMyReservations = async () => {
  const response = await api.get("/reservations/my");
  return response.data;
};

export const createReservation = async (machineId, startTime, endTime) => {
  const response = await api.post("/reservations", {
    machineId: Number(machineId),
    startTime,
    endTime,
  });

  return response.data;
};

export const deleteReservation = async (reservationId) => {
  const response = await api.delete(`/reservations/${reservationId}`);
  return response.data;
};