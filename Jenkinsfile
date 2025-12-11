pipeline {
    agent any

    environment {
        // Credentials from Jenkins
        GITHUB_TOKEN = credentials('github-jenkins-token')
        NEXUS_CREDS = credentials('nexus-admin-pass')
        SONAR_TOKEN = credentials('sonar-token')

        // Nexus & SonarQube URLs
        NEXUS_URL = "http://localhost:8082"
        NEXUS_REPO = "releases"
        SONARQUBE_URL = "http://localhost:9000"
        TOMCAT_USER = "admin"      // Tomcat manager username
        TOMCAT_PASS = "admin"      // Tomcat manager password
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: "https://github.com/jagathpraneshcoder/your-repo.git", credentialsId: 'github-jenkins-token'
            }
        }

        stage('Build & Unit Tests') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Run Selenium Headless Test') {
            steps {
                sh 'mvn test -Dtest=com.visualpathit.account.SeleniumHeadlessTest'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=vprofile -Dsonar.host.url=${SONARQUBE_URL} -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Deploy WAR to Nexus') {
            steps {
                sh """
                mvn deploy \
                -DrepositoryId=${NEXUS_REPO} \
                -Durl=${NEXUS_URL}/repository/${NEXUS_REPO}/ \
                -Dnexus.username=${NEXUS_CREDS_USR} \
                -Dnexus.password=${NEXUS_CREDS_PSW}
                """
            }
        }

        stage('Download WAR from Nexus') {
            steps {
                sh """
                wget -O target/vprofile.war \
                ${NEXUS_URL}/repository/${NEXUS_REPO}/com/visualpathit/vprofile/v2/vprofile-2.war \
                --user=${NEXUS_CREDS_USR} --password=${NEXUS_CREDS_PSW}
                """
            }
        }

        stage('Deploy WAR to Tomcat') {
            steps {
                sh """
                curl -u ${TOMCAT_USER}:${TOMCAT_PASS} \
                -T target/vprofile.war \
                http://localhost:8080/manager/text/deploy?path=/vprofile&update=true
                """
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}
