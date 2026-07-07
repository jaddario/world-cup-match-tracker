const teamFilter = document.getElementById('teamFilter');
const dateFilter = document.getElementById('dateFilter');
const stageFilter = document.getElementById('stageFilter');
const refreshButton = document.getElementById('refreshButton');
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
    matchesTableBody.innerHTML = '<tr><td class="empty" colspan="6">No matches match the current filters.</td></tr>';
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
        </tr>
      `;
    })
    .join('');
}

function setStatus(message) {
  status.textContent = message;
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

loadMatches();
