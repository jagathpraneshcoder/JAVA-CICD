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
        TOMCAT_USER = "admin"
        TOMCAT_PASS = "admin"
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

        /* ---------- 2. BUILD ---------- */
        stage('Build & Unit Tests') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }

        /* ---------- 3. SELENIUM TESTS ---------- */
        stage('Run Selenium Headless Test') {
            steps {
                sh 'mvn test -Dtest=com.visualpathit.account.SeleniumHeadlessTest'
            }
        }

        /* ---------- 4. SONARQUBE ANALYSIS ---------- */
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                        mvn sonar:sonar \
                            -Dsonar.projectKey=vprofile \
                            -Dsonar.host.url=${SONARQUBE_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        /* ---------- 5. DEPLOY TO NEXUS ---------- */
        stage('Deploy WAR to Nexus') {
            steps {
                // ✅ No credentials here; Maven now uses ~/.m2/settings.xml
                sh '''
                    mvn clean deploy -DskipTests \
                        --settings ~/.m2/settings.xml
                '''
                echo "✅ WAR uploaded to Nexus repository successfully."
            }
        }

        /* ---------- 6. DOWNLOAD FROM NEXUS ---------- */
        stage('Download WAR from Nexus') {
            steps {
                sh '''
                    wget -O target/vprofile.war \
                    ${NEXUS_URL}/repository/${NEXUS_REPO}/com/visualpathit/vprofile/1.0.0/vprofile-1.0.0.war \
                    --user=admin --password=Jagath@2003
                '''
                echo "✅ WAR downloaded from Nexus successfully."
            }
        }

        /* ---------- 7. DEPLOY TO TOMCAT ---------- */
        stage('Deploy WAR to Tomcat') {
            steps {
                sh '''
                    curl -u ${TOMCAT_USER}:${TOMCAT_PASS} \
                        -T target/vprofile.war \
                        http://localhost:8080/manager/text/deploy?path=/vprofile&update=true
                '''
                echo "✅ WAR deployed to Tomcat successfully."
            }
        }
    }

    /* ---------- 8. POST ACTIONS ---------- */
    post {
        always {
            echo "📦 Archiving build artifacts..."
            archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}
