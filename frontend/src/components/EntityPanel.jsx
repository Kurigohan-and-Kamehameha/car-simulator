import React, { useState } from 'react';

const EntityPanel = ({ 
  graph, 
  gameStates, 
  activeEntityId, 
  setActiveEntityId, 
  onCreateEntity, 
  onRemoveEntity 
}) => {
  const [selectedNodeId, setSelectedNodeId] = useState('');

  // Find all free nodes: nodes that are not the current position of any entity
  // (In a more complex scenario, we'd also check targets, but position is sufficient if cars are stationary at start)
  const occupiedNodes = new Set();
  Object.values(gameStates).forEach(state => {
    // We try to match by exact coordinates, but ideally the backend would provide current node ID.
    // Assuming backend nodes are fixed, we can just disable by checking if any car is exactly at the node's x,y.
    // Or we just find the closest node. Since we don't have node IDs in gameState, wait...
    // Let's check what gameState provides. We know the previous code was doing:
    // `gameState.x` and `gameState.y`
  });

  // Actually, wait, let's check what's in gameStateDTO. We have `position` or `x, y`.
  // Wait, if we just need to send `nodeId`, let's list all nodes and let the user select.
  // But wait, the prompt says "Belegte nodes sollen ausgegraut sein. die informationen über freie Nodes findet man in den positioncomponents der entitäten."
  // If `gameState` has `x, y`, we can match them to `node.x, node.y`.

  const getOccupiedNodeIds = () => {
    if (!graph || !graph.nodes) return new Set();
    const occupied = new Set();
    
    // Check all entities' positions
    Object.values(gameStates).forEach(state => {
      if (state.x !== undefined && state.y !== undefined) {
        // Find the node that matches this x,y exactly or closely
        const node = graph.nodes.find(n => {
          const dx = n.x - state.x;
          const dy = n.y - state.y;
          return Math.sqrt(dx * dx + dy * dy) < 5; // small threshold
        });
        if (node) {
          occupied.add(node.id);
        }
      }
    });
    return occupied;
  };

  const occupiedNodeIds = getOccupiedNodeIds();

  const handleAdd = () => {
    if (selectedNodeId) {
      onCreateEntity(selectedNodeId);
      setSelectedNodeId(''); // reset
    }
  };

  const entitiesList = Object.keys(gameStates).map(id => ({
    id: parseInt(id),
    state: gameStates[id]
  }));

  return (
    <div className="entity-panel">
      <h2>Entities</h2>
      
      <div className="add-entity-section">
        <label>Add New Car:</label>
        <div className="add-controls">
          <select 
            value={selectedNodeId} 
            onChange={(e) => setSelectedNodeId(e.target.value)}
          >
            <option value="" disabled>Select a Start Node</option>
            {graph?.nodes && graph.nodes.map(node => {
              const isOccupied = occupiedNodeIds.has(node.id);
              return (
                <option key={node.id} value={node.id} disabled={isOccupied}>
                  {node.id} {isOccupied ? '(Occupied)' : ''}
                </option>
              );
            })}
          </select>
          <button 
            className="action-btn add-btn" 
            onClick={handleAdd}
            disabled={!selectedNodeId}
            title="Add Entity"
          >
            ➕
          </button>
        </div>
      </div>

      <div className="entity-list-section">
        <h3>Active Entities ({entitiesList.length})</h3>
        <div className="entity-list">
          {entitiesList.length === 0 ? (
            <div className="no-entities">No entities active.</div>
          ) : (
            entitiesList.map(entity => (
              <div 
                key={entity.id} 
                className={`entity-item ${activeEntityId === entity.id ? 'active' : ''}`}
                onClick={() => setActiveEntityId(entity.id)}
              >
                <div className="entity-info">
                  <span className="entity-name">car #{entity.id}</span>
                </div>
                <button 
                  className="delete-btn" 
                  onClick={(e) => {
                    e.stopPropagation(); // prevent setting active
                    onRemoveEntity(entity.id);
                  }}
                  title="Remove Entity"
                >
                  ❌
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default EntityPanel;
