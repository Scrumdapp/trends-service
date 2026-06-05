const DAYS = 100;

const GROUPS = [1, 2, 3, 4];
const USERS = [1, 2, 3, 4, 5, 6];
const SESSIONS_PER_DAY = 4;

let sessionId = 1;
let checkpointId = 1;

const sessionSql = [];
const checkpointSql = [];

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomComment() {
    const comments = [
        'Good progress',
        'Blocked on deployment',
        'Need code review',
        'Everything on track',
        'Waiting for feedback',
        'Completed planned work'
    ];

    return comments[randomInt(0, comments.length - 1)];
}

for (let dayOffset = 0; dayOffset < DAYS; dayOffset++) {
    const date = new Date();
    date.setDate(date.getDate() - dayOffset);

    const dateString = date.toISOString().split('T')[0];

    for (const groupId of GROUPS) {
        for (let sessionNumber = 0; sessionNumber < SESSIONS_PER_DAY; sessionNumber++) {

            const hour = 9 + sessionNumber * 2;

            const sessionName = `Checkpoint ${sessionNumber + 1}`;

            sessionSql.push(`
INSERT INTO checkpoint_sessions
(id, group_id, group_user_id, created_date, start_time, duration_minutes, name)
VALUES
(
    ${sessionId},
    ${groupId},
    1,
    '${dateString}',
    '${String(hour).padStart(2, '0')}:00:00',
    15,
    '${sessionName}'
);`.trim());

            for (const userId of USERS) {
                const presence = randomInt(1, 5);
                const stars = randomInt(1, 5);

                const impediment =
                    Math.random() < 0.25
                        ? 'Blocked by dependency'
                        : null;

                const comment = randomComment();

                checkpointSql.push(`
INSERT INTO check_point
(id, checkpoint_session_id, group_user_id, impediment, presence, stars, comment)
VALUES
(
    ${checkpointId},
    ${sessionId},
    ${userId},
    ${impediment ? `'${impediment}'` : 'NULL'},
    ${presence},
    ${stars},
    '${comment}'
);`.trim());

                checkpointId++;
            }

            sessionId++;
        }
    }
}

console.log('-- CHECKPOINT SESSIONS');
console.log(sessionSql.join('\n'));

console.log('\n\n-- CHECKPOINTS');
console.log(checkpointSql.join('\n'));