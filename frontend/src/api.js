import axios from 'axios';

const API_BASE = '/api/game';

export const getGraph = async () => {
  const response = await axios.get(`${API_BASE}/graph`);
  return response.data;
};

export const setDirection = async (nodeId, id) => {
  await axios.post(`${API_BASE}/direction?nodeId=${nodeId}&id=${id}`);
};

export const setColor = async (color, id) => {
  await axios.post(`${API_BASE}/color?color=${encodeURIComponent(color)}&id=${id}`);
};

export const setEngine = async (engineType, id) => {
  await axios.post(`${API_BASE}/engine?engineType=${engineType}&id=${id}`);
};

export const setSpeed = async (speed, id) => {
  await axios.post(`${API_BASE}/speed?speed=${speed}&id=${id}`);
};

export const saveGame = async (path) => {
  await axios.post(`${API_BASE}/save?path=${encodeURIComponent(path)}`);
};

export const loadGame = async (path) => {
  await axios.post(`${API_BASE}/load?path=${encodeURIComponent(path)}`);
};

export const createEntity = async (nodeId) => {
  const response = await axios.post(`${API_BASE}/createEntity?nodeId=${nodeId}`);
  return response.data;
};

export const removeEntity = async (id) => {
  await axios.delete(`${API_BASE}/removeEntity?id=${id}`);
};
