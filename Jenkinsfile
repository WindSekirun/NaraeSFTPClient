@Library('jenkins-shared-library')_
pipeline {
  agent any
  stages {
    stage ('Start') {
      steps {
        sendNotifications 'STARTED'
      }
    }
    stage('Make Environment') {
      parallel {
        stage('Touch local.properties') {
          steps {
            sh 'echo "sdk.dir=/opt/android-sdk-linux" >> local.properties'
          }
        }
        stage('Touch google-services.json') {
                  steps {
                    sh 'echo $GOOGLE_SERVICES_JSON | base64 --decode --ignore-garbage > demo/google-services.json'
                  }
                }
        stage('Display directory') {
          steps {
            sh 'ls -la'
          }
        }
      }
    }
    stage('permission') {
      steps {
        sh 'chmod 777 gradlew'
      }
    }
    stage('assembleDebug') {
      steps {
        sh './gradlew --no-daemon assembleDebug --stacktrace'
      }
    }
  }
  post {
    always {
      sendNotifications currentBuild.result
    }
  }
  environment {
      GOOGLE_SERVICES_JSON = 'ewogICJwcm9qZWN0X2luZm8iOiB7CiAgICAicHJvamVjdF9udW1iZXIiOiAiNzE5 NzA2MDUxOTMwIiwKICAgICJmaXJlYmFzZV91cmwiOiAiaHR0cHM6Ly9yeHNvY2lh bGxvZ2luLTUwMzRlLmZpcmViYXNlaW8uY29tIiwKICAgICJwcm9qZWN0X2lkIjog InJ4c29jaWFsbG9naW4tNTAzNGUiLAogICAgInN0b3JhZ2VfYnVja2V0IjogInJ4 c29jaWFsbG9naW4tNTAzNGUuYXBwc3BvdC5jb20iCiAgfSwKICAiY2xpZW50Ijog WwogICAgewogICAgICAiY2xpZW50X2luZm8iOiB7CiAgICAgICAgIm1vYmlsZXNk a19hcHBfaWQiOiAiMTo3MTk3MDYwNTE5MzA6YW5kcm9pZDo1ZTAwYzI4OTg5OTdj NGJiIiwKICAgICAgICAiYW5kcm9pZF9jbGllbnRfaW5mbyI6IHsKICAgICAgICAg ICJwYWNrYWdlX25hbWUiOiAiY29tLmdpdGh1Yi53aW5kc2VraXJ1bi5yeHNvY2lh bGxvZ2luLnRlc3QiCiAgICAgICAgfQogICAgICB9LAogICAgICAib2F1dGhfY2xp ZW50IjogWwogICAgICAgIHsKICAgICAgICAgICJjbGllbnRfaWQiOiAiNzE5NzA2 MDUxOTMwLXJjcmZ1ZmpkcXBpazI2MzhnaDNlcGRoanBtdDJkYXZxLmFwcHMuZ29v Z2xldXNlcmNvbnRlbnQuY29tIiwKICAgICAgICAgICJjbGllbnRfdHlwZSI6IDMK ICAgICAgICB9CiAgICAgIF0sCiAgICAgICJhcGlfa2V5IjogWwogICAgICAgIHsK ICAgICAgICAgICJjdXJyZW50X2tleSI6ICJBSXphU3lBRmFWMXg3TktMSm4wWU1m WmdYeEx1WUZLNGJ6dS1FRHciCiAgICAgICAgfQogICAgICBdLAogICAgICAic2Vy dmljZXMiOiB7CiAgICAgICAgImFuYWx5dGljc19zZXJ2aWNlIjogewogICAgICAg ICAgInN0YXR1cyI6IDEKICAgICAgICB9LAogICAgICAgICJhcHBpbnZpdGVfc2Vy dmljZSI6IHsKICAgICAgICAgICJzdGF0dXMiOiAxLAogICAgICAgICAgIm90aGVy X3BsYXRmb3JtX29hdXRoX2NsaWVudCI6IFtdCiAgICAgICAgfSwKICAgICAgICAi YWRzX3NlcnZpY2UiOiB7CiAgICAgICAgICAic3RhdHVzIjogMgogICAgICAgIH0K ICAgICAgfQogICAgfSwKICAgIHsKICAgICAgImNsaWVudF9pbmZvIjogewogICAg ICAgICJtb2JpbGVzZGtfYXBwX2lkIjogIjE6NzE5NzA2MDUxOTMwOmFuZHJvaWQ6 MjQ1ZDBhM2IzNDhmYTA1YyIsCiAgICAgICAgImFuZHJvaWRfY2xpZW50X2luZm8i OiB7CiAgICAgICAgICAicGFja2FnZV9uYW1lIjogImNvbS5naXRodWIud2luZHNl a2lydW4uc29jaWFsbG9naW50ZXN0IgogICAgICAgIH0KICAgICAgfSwKICAgICAg Im9hdXRoX2NsaWVudCI6IFsKICAgICAgICB7CiAgICAgICAgICAiY2xpZW50X2lk IjogIjcxOTcwNjA1MTkzMC1qYWFwbGRrYTdkdGMxMm5hampxYXJjbjNyanNoMmhm dC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsCiAgICAgICAgICAiY2xpZW50 X3R5cGUiOiAxLAogICAgICAgICAgImFuZHJvaWRfaW5mbyI6IHsKICAgICAgICAg ICAgInBhY2thZ2VfbmFtZSI6ICJjb20uZ2l0aHViLndpbmRzZWtpcnVuLnNvY2lh bGxvZ2ludGVzdCIsCiAgICAgICAgICAgICJjZXJ0aWZpY2F0ZV9oYXNoIjogImM3 ZDI5ZmVjZWY4NGEyZGU5YWI4MWI5NGY1YWNkYmEwOTcyODM4ZTgiCiAgICAgICAg ICB9CiAgICAgICAgfSwKICAgICAgICB7CiAgICAgICAgICAiY2xpZW50X2lkIjog IjcxOTcwNjA1MTkzMC1yY3JmdWZqZHFwaWsyNjM4Z2gzZXBkaGpwbXQyZGF2cS5h cHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsCiAgICAgICAgICAiY2xpZW50X3R5 cGUiOiAzCiAgICAgICAgfQogICAgICBdLAogICAgICAiYXBpX2tleSI6IFsKICAg ICAgICB7CiAgICAgICAgICAiY3VycmVudF9rZXkiOiAiQUl6YVN5QUZhVjF4N05L TEpuMFlNZlpnWHhMdVlGSzRienUtRUR3IgogICAgICAgIH0KICAgICAgXSwKICAg ICAgInNlcnZpY2VzIjogewogICAgICAgICJhbmFseXRpY3Nfc2VydmljZSI6IHsK ICAgICAgICAgICJzdGF0dXMiOiAxCiAgICAgICAgfSwKICAgICAgICAiYXBwaW52 aXRlX3NlcnZpY2UiOiB7CiAgICAgICAgICAic3RhdHVzIjogMiwKICAgICAgICAg ICJvdGhlcl9wbGF0Zm9ybV9vYXV0aF9jbGllbnQiOiBbCiAgICAgICAgICAgIHsK ICAgICAgICAgICAgICAiY2xpZW50X2lkIjogIjcxOTcwNjA1MTkzMC1yY3JmdWZq ZHFwaWsyNjM4Z2gzZXBkaGpwbXQyZGF2cS5hcHBzLmdvb2dsZXVzZXJjb250ZW50 LmNvbSIsCiAgICAgICAgICAgICAgImNsaWVudF90eXBlIjogMwogICAgICAgICAg ICB9CiAgICAgICAgICBdCiAgICAgICAgfSwKICAgICAgICAiYWRzX3NlcnZpY2Ui OiB7CiAgICAgICAgICAic3RhdHVzIjogMgogICAgICAgIH0KICAgICAgfQogICAg fQogIF0sCiAgImNvbmZpZ3VyYXRpb25fdmVyc2lvbiI6ICIxIgp9'
  }
}