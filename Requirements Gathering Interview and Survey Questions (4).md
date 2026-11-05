# Requirements Gathering: Interview and Survey Questions

**Project:** E-learning authoring platform (SCORM, H5P and xAPI) **Role:** Q&A Engineer **Course:** Introduction to Software Engineering

## 1. Project Overview

The proposed system is a platform that helps teachers and instructors create interactive, gamified learning content without technical expertise. Content can be built in two ways:

- **Drag-and-drop editing:** the instructor assembles lessons from ready-made components in a visual editor.
- **AI generation:** the instructor uploads learning material and the AI generates the components automatically.

The finished content is exported in the format the instructor needs: **SCORM**, **H5P** or **xAPI**, for use in learning management systems (LMS).

## 2. Method and Approach

We use a **mixed method**:

- **Interviews** for teachers, instructional designers and administrators. These groups have deep, varied workflows, so open conversation produces richer answers.
- **Survey** for students and a wider pool of teachers. A survey reaches more people and gives numbers we can summarize.

Questions move from open to specific. Open questions come first so we do not lead respondents toward the features we already have in mind.

## 3. Stakeholder Analysis

| Stakeholder | Role in the system | Interest | Influence | How we gather data |
| --- | --- | --- | --- | --- |
| Teachers and instructors | Primary users; create and edit content | Save time, engage students | High | Interview and survey |
| Instructional designers | Power users; build complex courses | Control, standards compliance | High | Interview |
| Students | Consume the content | Engaging, accessible lessons | Medium | Survey |
| LMS administrators / IT staff | Import, host and maintain content | Compatibility, security, low support load | High | Interview |
| School or institution management | Approve purchase and adoption | Cost, outcomes, policy compliance | High | Interview |
| Parents and guardians | Indirectly affected | Student data privacy | Low | Optional survey |
| Regulators and policy bodies | Set legal limits | Data protection (GDPR, FERPA) | Medium | Document review |

## 4. Interview and Survey Questions

Each question includes the reasoning behind it. Questions are grouped by the six areas a requirement must address.

### Section 0: Screening and Background (all respondents)

**Purpose:** Confirms who the respondent is so we can place their answers in the correct stakeholder group.

| # | Question | Reasoning |
| --- | --- | --- |
| 1 | What is your role (teacher, instructional designer, LMS admin, student, school manager)? | Classifies the respondent for the stakeholder analysis. |
| 2 | How many years have you created or used digital learning content? | Experience level shapes how much we must simplify the interface. |

### Section 1: What must the system do? (Tasks and functions)

**Purpose:** Discover the real workflow and pain points before assuming features.

| # | Question | Reasoning |
| --- | --- | --- |
| 3 | Walk me through how you currently create an online lesson, from start to finish. | An open question reveals the actual process and the tools already in use. |
| 4 | What is the most time-consuming or frustrating part of that process? | Pain points identify the features with the most value. |
| 5 | Which interactive elements do you use or wish you could use (quizzes, drag-and-drop, videos, branching scenarios, badges)? | Builds the component library and gamification features from real demand. |
| 6 | If an AI generated components from your materials, what would you need to see before trusting and publishing them? | Defines review, edit and approval requirements for the AI feature. |
| 7 | Which export formats do you need (SCORM 1.2, SCORM 2004, xAPI, H5P), and why? | Confirms which standards are essential and which are optional. |

### Section 2: Who will use it, and who else is affected? (The people)

**Purpose:** Support the stakeholder analysis and find hidden stakeholders.

| # | Question | Reasoning |
| --- | --- | --- |
| 8 | Who else is involved in creating, approving or delivering your courses (admins, IT, reviewers)? | Reveals stakeholders we may have missed. |
| 9 | Who decides which tools your institution adopts? | Identifies the real decision maker, who may not be the end user. |
| 10 | How do students currently react to your online content, and what would make it more engaging for them? | Captures the student perspective through the teacher's experience. |
| 11 | How comfortable are you with technical tools (low, medium, high)? | Determines the level of simplicity and onboarding needed. |

### Section 3: How well must it work? (Speed, reliability, ease of use, security)

**Purpose:** Convert vague wishes such as "easy" into measurable quality targets.

| # | Question | Reasoning |
| --- | --- | --- |
| 12 | How long should it take to build a 10-minute lesson for the tool to feel worthwhile? | Gives a measurable performance and usability target. |
| 13 | How much downtime or data loss could you tolerate? | Sets reliability and backup requirements. |
| 14 | What would make a tool feel too hard to learn, and how much training time is acceptable? | Defines ease-of-use and onboarding limits. |
| 15 | What are your concerns about student data privacy and about uploading materials to an AI? | Surfaces security and trust requirements specific to AI. |
| 16 | Do you need accessibility support (screen readers, captions, keyboard navigation)? | Establishes accessibility requirements, often a legal obligation. |

### Section 4: How much, how often, how many? (Volumes and frequency)

**Purpose:** Drive capacity, storage and pricing decisions.

| # | Question | Reasoning |
| --- | --- | --- |
| 17 | How many courses or lessons do you create per term? | Estimates how often users edit, save and export. |
| 18 | How many students typically take one course, and how many at peak? | Informs expected load on exported content and tracking (xAPI) data. |
| 19 | What is the typical size and type of your source materials (PDFs, slides, videos)? | Sets upload limits and AI processing requirements. |

### Section 5: Where and on what? (Devices, internet, language, location)

**Purpose:** Determine platform choice, compatibility and localization.

| # | Question | Reasoning |
| --- | --- | --- |
| 20 | Which LMS do you use (Moodle, Canvas, Blackboard, other)? | Exports must work in the LMSs our users actually have. |
| 21 | Which devices and browsers do you and your students use? | Defines supported platforms and responsive design needs. |
| 22 | How reliable is your internet connection, and do you need offline use? | Decides whether offline or low-bandwidth support is required. |
| 23 | In which languages must content be created and delivered, including right-to-left languages? | Determines localization and text-direction support. |

### Section 6: Under what limits? (Budget, deadline, laws, existing systems)

**Purpose:** Surface constraints that could eliminate a feature early.

| # | Question | Reasoning |
| --- | --- | --- |
| 24 | What budget or pricing model (free, per user, institutional license) is realistic for you? | Shapes the business model and feature scope. |
| 25 | Are there laws or policies you must follow (GDPR, FERPA, institutional AI rules)? | Identifies legal and policy constraints on data and AI use. |
| 26 | Which existing tools or content must this integrate with or import from? | Reveals integration requirements and migration needs. |
| 27 | What is your deadline or adoption timeline? | Helps prioritize what must be in the first release. |

### Closing Questions (all respondents)

| # | Question | Reasoning |
| --- | --- | --- |
| 28 | If you could only get three features, which would they be? | Forces prioritization and supports the findings slide. |
| 29 | Is there anything we did not ask that we should have? | Catches requirements we did not anticipate. |

## 5. Mapping Questions to Requirement Areas

| Requirement area | Questions |
| --- | --- |
| What must the system do? | 3, 4, 5, 6, 7 |
| Who will use it, and who else is affected? | 1, 2, 8, 9, 10, 11 |
| How well must it work? | 12, 13, 14, 15, 16 |
| How much, how often, how many? | 17, 18, 19 |
| Where and on what? | 20, 21, 22, 23 |
| Under what limits? | 24, 25, 26, 27 |
| Prioritization and gaps | 28, 29 |

## 6. Planned Data Analysis

- **Open answers:** group similar responses into themes and count how often each appears.
- **Closed answers:** calculate percentages for each option.
- **Stakeholder view:** compare answers across groups, since teachers, admins and students may want different things.
- **Prioritization:** use question 28 to rank features by how many respondents named them.
- **Output:** the findings feed the presentation, including the summary of data collected, the conclusion and answers to the six requirement questions.
