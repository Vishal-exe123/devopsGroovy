job("ak1_groovy"){
   description("My job1")
  scm{
     github('Vishal-exe123/devopsalTask6' , 'master')
}
steps{
  shell('sudo  cp  -rvf  *  /vish/jenkins')
}
triggers{
   githubPushTrigger()
}
}

job("ak2_groovy"){
  steps{
   shell('''
     if   sudo  ls  /vish/jenkins  |  grep  html
    then    
             if  sudo kubectl  get pods --selector  "app in (apache)"  |  grep  apachepod
             then
                   POD1=$(sudo  kubectl get pods  -l app=apache  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD1
                  sudo   kubectl  cp   /vish/jenkins/index.html  $POD1:/var/www/html
                  sudo   kubectl  cp   /vish/jenkins/vish.html  $POD1:/var/www/html
          else
                  sudo  kubectl   apply  -f  /vish/jenkins/apachepod.yml
                   POD1=$(sudo  kubectl get pods  -l app=apache  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD1
                  sudo   kubectl  cp   /vish/jenkins/index.html  $POD1:/var/www/html
                  sudo   kubectl  cp   /vish/jenkins/vish.html  $POD1:/var/www/html
         fi
    else  
          echo  "no html file" 
    fi

    if   sudo  ls  /vish/jenkins  |  grep  php
    then    
             if  sudo kubectl  get pods --selector  "app in (php)"  |  grep  phppod
             then
                   POD2=$(sudo  kubectl get pods  -l app=php  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD2
                  sudo   kubectl  cp   /vish/jenkins/vishal.html  $POD2:/var/www/html
          else
                  sudo  kubectl   apply  -f  /vish/jenkins/phppod.yml
                   POD2=$(sudo  kubectl get pods  -l app=php  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD2
                  sudo   kubectl  cp   /vish/jenkins/vishal.html  $POD2:/var/www/html
         fi
    else  
          echo  "no html file" 
    fi
''')
}
  triggers{
    upstream('ak1_groovy' , 'SUCCESS')
}
}

job("ak3_groovy"){
   steps{
         shell('''
          status=$(curl  -o  /dev/null  -s  -w  "%{httpd_code}"   http://192.168.99.101:31000)
          if  [[ $status ==  200 ]]
          then
               echo  "apache html is running"
               exit  0
         else
              exit 1
         fi

         status=$(curl  -o  /dev/null  -s  -w  "%{httpd_code}"   http://192.168.99.101:32000)
          if  [[ $status ==  200 ]]
          then
               echo  "apache php is running"
               exit  0
         else
              exit 1
         fi
''')
}
  triggers{
    upstream('ak2_groovy' , 'SUCCESS')
}
  publishers {
    extendEmail {
        recipientList('vishalyadav831874@gmail.com')
        defaultSubject('Job status')
               attachBuildLog(attachBuildLog = true)
        defaultContent('Status Report')
        contentType('text/html/php')
       triggers {
            always {
            subject('build Status')
            content('Body')
           sendTo{
              developers()
             recipientsList()
            }
        }
    }
 }
}
}
