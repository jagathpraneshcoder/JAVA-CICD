pipeline {
    agent any

    environment {
        // Jenkins credentials
        GITHUB_TOKEN = credentials('github-jenkins-token')
        SONAR_TOKEN  = credentials('sonar-token')

        // URLs
        NEXUS_URL = "http://localhost:8082"
        NEXUS_REPO = "releases"
        SONARQUBE_URL = "http://localhost:9000"

        // Tomcat credentials from Jenkins (Username + Password)
        TOMCAT_CREDS = credentials('tomcat-credentials')
    }

    stages {

        /* ---------- 1. CHECKOUT ---------- */
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/jagathpraneshcoder/JAVA-CICD.git',
                    credentialsId: 'github-jenkins-token'
            }
        }

        /* ---------- 2. BUILD & UNIT TESTS ---------- */
        stage('Build & Unit Tests') {
            steps {
                echo "⚙️ Building project and running unit tests..."
                sh 'mvn clean verify -DskipTests=false'
            }
            post {
                always {
                    // publish test results
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
                unsuccessful {
                    echo "❌ Build or unit tests failed — stopping pipeline."
                    error("Build failed. Skipping deployment.")
                }
            }
        }

        /* ---------- 3. SONARQUBE ANALYSIS ---------- */
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    echo "🔍 Running SonarQube code quality analysis..."
                    sh '''
                        mvn sonar:sonar \
                            -Dsonar.projectKey=vprofile \
                            -Dsonar.host.url=${SONARQUBE_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        /* ---------- 4. DEPLOY WAR TO NEXUS ---------- */
        stage('Deploy WAR to Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-admin-pass',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    echo "📦 Uploading WAR to Nexus..."
                    sh 'mvn clean deploy -DskipTests --settings settings.xml'
                }
            }
        }

        /* ---------- 5. DOWNLOAD WAR FROM NEXUS ---------- */
        stage('Download WAR from Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-admin-pass',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    echo "⬇️ Downloading WAR from Nexus..."
                    sh '''
                        wget -O target/vprofile.war \
                        ${NEXUS_URL}/repository/${NEXUS_REPO}/com/visualpathit/vprofile/1.0.0/vprofile-1.0.0.war \
                        --user=${NEXUS_USER} --password=${NEXUS_PASS}
                    '''
                }
                echo "✅ WAR downloaded successfully."
            }
        }

        /* ---------- 6. DEPLOY WAR TO TOMCAT ---------- */
        stage('Deploy WAR to Tomcat') {
            steps {
                echo "🚀 Deploying WAR to Tomcat..."
                sh '''
                    curl -u ${TOMCAT_CREDS_USR}:${TOMCAT_CREDS_PSW} \
                        -T target/vprofile.war \
                        http://localhost:8080/manager/text/deploy?path=/vprofile&update=true
                '''
                echo "✅ WAR deployed to Tomcat successfully."
            }
        }

        /* ---------- 7. RUN SELENIUM TESTS (POST DEPLOY VALIDATION) ---------- */
        stage('Run Selenium Headless Test') {
            steps {
                echo "🧪 Running Selenium headless browser test on deployed app..."
                // Wait for Tomcat to finish deployment
                sh 'sleep 10'
                // Run Selenium tests (don’t fail pipeline if browser test fails)
                sh 'mvn test -Dtest=com.visualpathit.account.SeleniumHeadlessTest || true'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
    }

    /* ---------- POST-STEPS ---------- */
    post {
        always {
            echo "📦 Archiving final WAR file..."
            archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
            echo "✅ Pipeline completed."
        }
    }
}
