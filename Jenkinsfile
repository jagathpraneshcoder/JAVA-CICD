pipeline {
    agent any

    environment {
        // Jenkins credentials for GitHub (if private repo)
        GITHUB_TOKEN = credentials('github-jenkins-token')
        // Tomcat username and password stored in Jenkins credentials
        TOMCAT_CREDS = credentials('tomcat-credentials')
        // Path to your local Tomcat webapps folder
        TOMCAT_WEBAPPS = "/opt/tomcat9/webapps"
        APP_NAME = "vprofile"
    }

    stages {

        /* ---------- 1. CHECKOUT ---------- */
        stage('Checkout') {
            steps {
                echo "🔄 Checking out code from GitHub..."
                git branch: 'main',
                    url: 'https://github.com/jagathpraneshcoder/JAVA-CICD.git',
                    credentialsId: 'github-jenkins-token'
            }
        }

        /* ---------- 2. BUILD WAR ---------- */
        stage('Build WAR') {
            steps {
                echo "⚙️ Building the WAR file..."
                sh 'mvn clean package -DskipTests'
            }
        }

        /* ---------- 3. DEPLOY TO TOMCAT ---------- */
        stage('Deploy WAR to Tomcat') {
            steps {
                echo "🚀 Deploying WAR to Tomcat..."
                sh '''
                    # Remove previous deployment
                    rm -rf ${TOMCAT_WEBAPPS}/${APP_NAME}

                    # Copy new WAR file
                    cp target/${APP_NAME}.war ${TOMCAT_WEBAPPS}/

                    # Wait a few seconds for Tomcat to unpack WAR
                    sleep 10
                '''
                echo "✅ Deployment complete. App should be accessible at http://localhost:8080/${APP_NAME}"
            }
        }
    }

    post {
        always {
            echo "📦 Archiving WAR file..."
            archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            echo "✅ Pipeline finished."
        }
    }
}
