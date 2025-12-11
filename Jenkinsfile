pipeline {
    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-8-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/jagathpraneshcoder/Java-CICD.git',
                    credentialsId: 'github-jenkins-token'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests=true'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Archive WAR File') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', onlyIfSuccessful: true
            }
        }

        stage('Deploy to Local Tomcat') {
        when {
            expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
        }
        steps {
            echo "🚀 Deploying WAR to local Tomcat..."
            sh '''
                echo "Stopping Tomcat..."
                sudo /opt/tomcat9/bin/shutdown.sh || true
    
                echo "Copying WAR file to webapps..."
                sudo cp target/*.war /opt/tomcat9/webapps/
    
                echo "Starting Tomcat..."
                sudo /opt/tomcat9/bin/startup.sh
    
                echo "Waiting for Tomcat to start..."
                sleep 10
    
                echo "✅ Deployment complete!"
            '''
        }
}
    }

    post {
        success {
            echo '✅ Build, Test, and Deployment Successful!'
        }
        failure {
            echo '❌ Build, Test, or Deployment Failure!'
        }
    }
}
