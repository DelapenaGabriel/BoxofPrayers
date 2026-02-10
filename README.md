<div align="center">
  <!-- Logo -->
  <img src="https://res.cloudinary.com/dujc4iuu8/image/upload/v1770675269/yjhegkbvrwnunjgwsja5.png" alt="Box of Prayers Logo" width="150"/>

  <!-- Title -->
  <h1 style="color:#efbf04; margin-top: 10px;">BOX OF PRAYERS</h1>

  <!-- Description -->
  <p style="max-width: 600px; font-size: 18px; line-height: 1.5;">
    Box of Prayers is a production-ready, faith-based community platform where users can submit prayer requests, engage with community content, and support one another through prayer.<br>
    Built as a full-stack web application emphasizing clean architecture, scalability, and a mobile-first user experience.
  </p>

  <!-- Live Link -->
  <p>🌐 <strong>Live Application:</strong> <a href="https://boxofprayers.com" target="_blank">https://boxofprayers.com</a></p>

  <!-- Tech Badges -->
  <p>
    <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
    <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
    <img src="https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D" alt="Vue.js"/>
    <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  </p>
</div>

<hr/>

<div style="max-width: 800px; margin: auto; font-size: 16px; line-height: 1.6;">

  <!-- Features -->
  <h2>✨ Features</h2>
  <ul>
    <li>🕊️ Submit and view prayer requests in a supportive community</li>
    <li>🔐 Secure user authentication and authorization</li>
    <li>📱 Mobile-first, iOS-style responsive UI</li>
    <li>⚡ Fast and reliable RESTful APIs</li>
    <li>🗃️ Structured relational data with PostgreSQL</li>
    <li>🚀 Deployed and maintained in a live production environment</li>
  </ul>

  <!-- Architecture -->
  <h2>🏗️ Architecture Overview</h2>
  <p>Box of Prayers follows a clean MVC + DAO architecture, separating concerns across layers for maintainability and scalability.</p>
  <pre style="background:#f6f8fa; padding:10px; border-radius:5px;">
Vue.js Frontend
      ↓
Spring Boot REST APIs
      ↓
DAO Layer (JdbcTemplate / SQLRowSet)
      ↓
PostgreSQL Database
  </pre>

  <!-- Tech Stack -->
  <h2>🛠️ Tech Stack</h2>
  <h3>Backend</h3>
  <ul>
    <li>Java</li>
    <li>Spring Boot & Spring Security</li>
    <li>JdbcTemplate & SQLRowSet</li>
    <li>RESTful APIs</li>
    <li>MVC & DAO patterns</li>
  </ul>

  <h3>Frontend</h3>
  <ul>
    <li>Vue.js</li>
    <li>Mobile-first responsive design</li>
    <li>Component-based architecture</li>
  </ul>

  <h3>Database</h3>
  <ul>
    <li>PostgreSQL</li>
    <li>Normalized relational schema</li>
    <li>Optimized queries for performance and data integrity</li>
  </ul>

  <h3>DevOps / Deployment</h3>
  <ul>
    <li>Live production deployment</li>
    <li>Environment-based configuration</li>
    <li>Continuous iteration based on real user usage</li>
  </ul>

  <!-- Security -->
  <h2>🔐 Security</h2>
  <ul>
    <li>Secure authentication and authorization workflows</li>
    <li>Protected routes and role-based access</li>
    <li>Backend validation and safe data handling</li>
  </ul>

  <!-- Getting Started -->
  <h2>📦 Getting Started (Local Development)</h2>
  <h3>Prerequisites</h3>
  <ul>
    <li>Java 17+</li>
    <li>Node.js & npm</li>
    <li>PostgreSQL</li>
  </ul>

  <h3>Backend Setup</h3>
  <pre style="background:#f6f8fa; padding:10px; border-radius:5px;">
git clone https://github.com/delapenagabriel/boxofprayers.git
cd boxofprayers/server
./mvnw spring-boot:run
  </pre>

  <h3>Frontend Setup</h3>
  <pre style="background:#f6f8fa; padding:10px; border-radius:5px;">
cd boxofprayers/client
npm install
npm run dev
  </pre>

  <h3>Environment Variables</h3>
  <p>Create <code>.env</code> files for backend and frontend with:</p>
  <ul>
    <li>Database connection details</li>
    <li>API base URLs</li>
    <li>Security secrets</li>
  </ul>

  <!-- Motivation -->
  <h2>🙌 Motivation</h2>
  <p>
    Box of Prayers was built to combine faith, technology, and community — providing a simple, safe place where people can share prayer requests and support one another.
  </p>

  <!-- License -->
  <h2>📄 License</h2>
  <p>This project is proprietary. Please contact the author for usage or contribution inquiries.</p>

  <!-- Author -->
  <h2>👤 Author</h2>
  <p>
    <strong>Gabriel Delepena</strong><br/>
    Full-Stack Software Engineer<br/>
    🌐 <a href="https://boxofprayers.com" target="_blank">https://boxofprayers.com</a>
  </p>

</div>
