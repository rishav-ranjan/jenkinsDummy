//def gitServiceURL = "https://github.com/WPPg2/avatar-reg"
//def gitCredentials = "8cf0000b-3991-4db0-a2d9-e157168d2cef"
//def buildTrigger = "NIGHTLY"
//def serviceConfigPath = "/home/ec2-user/avregPipPilot/pipeline/services"

def buildPublishTargetNode = 'Morpheus'
def deployAmiTargetNode = 'AMIBuilder'
def rootPomPath = "." 
def parentstageName = "Commit"



stage "${parentstageName}::BuildAndPublish"

    subJob1 = build  job: '../../wppCommon/subJobs/buildAndPublish'/*,
                    parameters: [
                        [$class: 'StringParameterValue', name: 'gitCredentials', value: gitCredentials ],
                        [$class: 'StringParameterValue', name: 'url', value: gitServiceURL ],
                        [$class: 'StringParameterValue', name: 'buildTrigger', value: buildTrigger ],
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'targetNode', value: buildPublishTargetNode],
						[$class: 'StringParameterValue', name: 'rootPomPath', value: rootPomPath],
                    ] ;
    
    // Returned Values
    def artifactURL = subJob1.description.tokenize('#')[0].tokenize('=')[1]
    def artifactVersion = subJob1.description.tokenize('#')[1].tokenize('=')[1]
    def commitID = subJob1.description.tokenize('#')[2].tokenize('=')[1]

    */
stage "${parentstageName}::Deploy"
    subJob2 = build  job: '../../wppCommon/subJobs/deploy'/*,
                    parameters: [
                        [$class: 'StringParameterValue', name: 'gitCredentials', value: gitCredentials ],
                        [$class: 'StringParameterValue', name: 'url', value: gitDeployURL ],
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'serviceConfigPath', value: serviceConfigPath ],
                        [$class: 'StringParameterValue', name: 'artifactURL', value: artifactURL ],
                        [$class: 'StringParameterValue', name: 'targetNode', value: deployAmiTargetNode ],
                    ] ;
                    
    // Returned Values                
    def instanceID = subJob2.description.tokenize('#')[0].tokenize('=')[1]
    println "the values are ${artifactURL}\t${artifactVersion}\t${instanceID}"*/
    
    
    
stage "${parentstageName}::CreateAMI"
    subJob3 = build  job: '../../wppCommon/subJobs/createAMI'/*,
                     parameters: [
                        [$class: 'StringParameterValue', name: 'gitCredentials', value: gitCredentials ],
                        [$class: 'StringParameterValue', name: 'url', value: gitDeployURL ],
                        [$class: 'StringParameterValue', name: 'artifactVersion', value: artifactVersion ],
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'serviceConfigPath', value: serviceConfigPath ],
                        [$class: 'StringParameterValue', name: 'instanceID', value: instanceID ],
                        [$class: 'StringParameterValue', name: 'commitID', value: commitID ],
                        [$class: 'StringParameterValue', name: 'targetNode', value: deployAmiTargetNode ],
                    ] ;
					
    // Returned Values                
    def amiID = subJob3.description.tokenize('#')[0].tokenize('=')[1]
	
	
currentBuild.setDescription("#amiID="+amiID+"#commitID="+commitID)*/