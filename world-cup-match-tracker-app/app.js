const teamFilter = document.getElementById('teamFilter');
const dateFilter = document.getElementById('dateFilter');
const stageFilter = document.getElementById('stageFilter');
const refreshButton = document.getElementById('refreshButton');
const createMatchForm = document.getElementById('createMatchForm');
const createMatchButton = document.getElementById('createMatchButton');
const status = document.getElementById('status');
const matchesTableBody = document.getElementById('matchesTableBody');

const apiBase = document.body.dataset.apiBase || 'http://localhost:8080';

function buildApiUrl() {
  const params = new URLSearchParams();

  if (teamFilter.value) {
    params.set('team', teamFilter.value);
  }

  if (dateFilter.value) {
    params.set('date', dateFilter.value);
  }

  if (stageFilter.value) {
    params.set('stage', stageFilter.value);
  }

  params.set('page', '0');
  params.set('size', '20');

  return `${apiBase}/api/matches?${params.toString()}`;
}

function renderMatches(matches) {
  if (!matches.length) {
    matchesTableBody.innerHTML = '<tr><td class="empty" colspan="7">No matches match the current filters.</td></tr>';
    return;
  }

  matchesTableBody.innerHTML = matches
    .map((match) => {
      const score = `${match.homeScore ?? '-'} - ${match.awayScore ?? '-'}`;
      const matchDate = match.matchDate ? new Date(match.matchDate).toLocaleDateString() : '—';

      return `
        <tr>
          <td>${match.homeTeam ?? '—'}</td>
          <td>${match.awayTeam ?? '—'}</td>
          <td>${score}</td>
          <td>${matchDate}</td>
          <td>${match.location ?? '—'}</td>
          <td>${match.stage ?? '—'}</td>
          <td>
            <button type="button" class="delete-button" data-id="${match.id ?? ''}">Delete</button>
          </td>
        </tr>
      `;
    })
    .join('');
}

function setStatus(message) {
  status.textContent = message;
}

function getCreatePayload() {
  return {
    homeTeam: document.getElementById('homeTeam').value.trim(),
    awayTeam: document.getElementById('awayTeam').value.trim(),
    homeScore: Number(document.getElementById('homeScore').value),
    awayScore: Number(document.getElementById('awayScore').value),
    matchDate: document.getElementById('matchDate').value,
    location: document.getElementById('location').value.trim(),
    stage: document.getElementById('createStage').value.trim()
  };
}

async function createMatch(event) {
  event.preventDefault();

  const payload = getCreatePayload();

  if (!payload.homeTeam || !payload.awayTeam || !payload.matchDate || !payload.location || !payload.stage) {
    setStatus('Please complete all required fields before creating a match.');
    return;
  }

  setStatus('Creating match…');
  createMatchButton.disabled = true;
  createMatchButton.textContent = 'Creating…';

  try {
    const response = await fetch(`${apiBase}/api/matches`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const errorPayload = await response.text();
      throw new Error(errorPayload || `Request failed with ${response.status}`);
    }

    createMatchForm.reset();
    await loadMatches();
    setStatus('Match created successfully.');
  } catch (error) {
    console.error(error);
    setStatus(`Unable to create match: ${error.message}`);
  } finally {
    createMatchButton.disabled = false;
    createMatchButton.textContent = 'Create match';
  }
}

async function deleteMatch(matchId) {
  if (!matchId) {
    return;
  }

  setStatus('Deleting match…');

  try {
    const response = await fetch(`${apiBase}/api/matches/${matchId}`, {
      method: 'DELETE'
    });

    if (!response.ok) {
      throw new Error(`Request failed with ${response.status}`);
    }

    await loadMatches();
    setStatus('Match deleted successfully.');
  } catch (error) {
    console.error(error);
    setStatus(`Unable to delete match: ${error.message}`);
  }
}

async function loadMatches() {
  setStatus('Loading matches…');

  try {
    const response = await fetch(buildApiUrl(), {
      headers: {
        Accept: 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Request failed with ${response.status}`);
    }

    const payload = await response.json();
    const matches = Array.isArray(payload.content) ? payload.content : [];

    renderMatches(matches);
    setStatus(matches.length ? `Showing ${matches.length} match${matches.length === 1 ? '' : 'es'}.` : 'No matches found for the current filters.');
  } catch (error) {
    console.error(error);
    renderMatches([]);
    setStatus(`Unable to load matches: ${error.message}`);
  }
}

[teamFilter, dateFilter, stageFilter].forEach((field) => {
  field.addEventListener('input', loadMatches);
  field.addEventListener('change', loadMatches);
});

refreshButton.addEventListener('click', loadMatches);
createMatchForm.addEventListener('submit', createMatch);

matchesTableBody.addEventListener('click', (event) => {
  const button = event.target.closest('.delete-button');
  if (!button) {
    return;
  }

  const matchId = button.getAttribute('data-id');
  deleteMatch(matchId);
});

loadMatches();
