pipeline {
    agent any

    environment {
        APP_NAME     = "vprofile"
        NEXUS_URL    = "http://localhost:8082"
        NEXUS_REPO   = "releases"
        TOMCAT_WEBAPPS = "/opt/tomcat9/webapps"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/jagathpraneshcoder/JAVA-CICD.git',
                    credentialsId: 'github-jenkins-token'
            }
        }

        stage('Build WAR') {
            steps {
                echo "⚙️ Building WAR file..."
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy WAR to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-admin-pass', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    echo "📦 Deploying WAR to Nexus..."
                    sh 'mvn deploy -DskipTests --settings settings.xml'
                }
            }
        }

        stage('Download WAR from Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-admin-pass', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    echo "⬇️ Downloading WAR from Nexus..."
                    sh """
                        wget -O target/${APP_NAME}.war \
                        ${NEXUS_URL}/repository/${NEXUS_REPO}/com/visualpathit/${APP_NAME}/1.0.0/${APP_NAME}-1.0.0.war \
                        --user=${NEXUS_USER} --password=${NEXUS_PASS}
                    """
                }
                echo "✅ WAR downloaded successfully."
            }
        }

        stage('Deploy WAR to Tomcat') {
            steps {
                echo "🚀 Deploying WAR to Tomcat..."
                sh """
                    # Remove previous deployment if exists
                    rm -rf ${TOMCAT_WEBAPPS}/${APP_NAME}

                    # Copy downloaded WAR to Tomcat
                    cp target/${APP_NAME}.war ${TOMCAT_WEBAPPS}/${APP_NAME}.war

                    # Wait for Tomcat to unpack the WAR
                    sleep 10
                """
                echo "✅ Deployment complete. App should be accessible at http://localhost:8080/${APP_NAME}"
            }
        }
    }

    post {
        always {
            echo "📦 Archiving WAR file..."
            archiveArtifacts artifacts: "target/${APP_NAME}.war", fingerprint: true
            echo "✅ Pipeline finished."
        }
    }
}
