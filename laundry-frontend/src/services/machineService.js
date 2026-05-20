import api from "./api";

export const getMachines = async () => {
  const response = await api.get("/machines");
  return response.data;
};

export const createMachine = async (type) => {
  const response = await api.post("/machines", {
    type,
    status: "AVAILABLE",
  });

  return response.data;
};

export const deleteMachine = async (machineId) => {
  const response = await api.delete(`/machines/${machineId}`);
  return response.data;
};

export const markMachineAsBroken = async (machineId) => {
  const response = await api.patch(`/machines/${machineId}/broken`);
  return response.data;
};

export const markMachineAsAvailable = async (machineId) => {
  const response = await api.patch(`/machines/${machineId}/available`);
  return response.data;
};
