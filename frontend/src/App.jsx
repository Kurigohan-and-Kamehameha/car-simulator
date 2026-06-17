import React, { useState, useEffect } from 'react';
import { getGraph, setDirection, createEntity, removeEntity } from './api';
import { GameWebSocket } from './websocket';
import CanvasRenderer from './components/CanvasRenderer';
import ControlPanel from './components/ControlPanel';
import EntityPanel from './components/EntityPanel';

function App() {
  const [graph, setGraph] = useState(null);
  const [gameStates, setGameStates] = useState({});
  const [activeEntityId, setActiveEntityId] = useState(null);
  
  useEffect(() => {
    // Function to fetch graph from backend
    const fetchGraph = () => getGraph().then(setGraph).catch(console.error);

    // Setup WebSockets
    const ws = new GameWebSocket();
    ws.connect(
      (state) => {
        setGameStates(prev => ({ ...prev, [state.id]: state }));
        setActiveEntityId(prev => prev === null ? state.id : prev);
      },
      (statesArray) => {
        const next = {};
        statesArray.forEach(s => next[s.id] = s);
        setGameStates(next);
        setActiveEntityId(prev => next[prev] ? prev : null);
      },
      () => {
        setGameStates({}); // Clear old entities on new connection
        fetchGraph();
      }
    );

    return () => {
      ws.disconnect();
    };
  }, []);

  const handleNodeClick = (nodeId) => {
    if (activeEntityId !== null) {
      setDirection(nodeId, activeEntityId).catch(console.error);
    }
  };

  const handleCreateEntity = async (nodeId) => {
    try {
      const newId = await createEntity(nodeId);
      setActiveEntityId(newId);
    } catch (err) {
      console.error(err);
    }
  };

  const handleRemoveEntity = async (id) => {
    try {
      await removeEntity(id);
      setGameStates(prev => {
        const next = { ...prev };
        delete next[id];
        return next;
      });
      if (activeEntityId === id) setActiveEntityId(null);
    } catch (err) {
      console.error(err);
    }
  };



  const activeGameState = activeEntityId !== null ? gameStates[activeEntityId] : null;

  return (
    <div className="app-container">
      <header className="header">
        <h1>Car Game Web UI</h1>
      </header>
      <main className="main-content">
        <aside className="sidebar left-sidebar">
          <EntityPanel 
            graph={graph}
            gameStates={gameStates}
            activeEntityId={activeEntityId}
            setActiveEntityId={setActiveEntityId}
            onCreateEntity={handleCreateEntity}
            onRemoveEntity={handleRemoveEntity}
          />
        </aside>
        <div className="game-view">
          <CanvasRenderer 
            graph={graph} 
            gameStates={gameStates} 
            onNodeClick={handleNodeClick} 
          />
        </div>
        <aside className="sidebar right-sidebar">
          <ControlPanel 
            gameState={activeGameState} 
            activeEntityId={activeEntityId} 
          />
        </aside>
      </main>
    </div>
  );
}

export default App;
