//incoming parameters - serviceName, buildTrigger

def serviceConfigBaseURL = "file:///jdata/jenkins/pipelineConfig/${serviceName}"
def amiID
def serviceCommitID
def loadTestCommitID

stage "CommitStage"

parallel(firstTask: {
    subJob1 = build  job: '../subUserJobs/Commit_Service',
                parameters: [
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'buildTrigger', value: buildTrigger ],
                        [$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL ],
                ];
	//returned values
    amiID = subJob1.description.tokenize('#')[0].tokenize('=')[1]
    serviceCommitID = subJob1.description.tokenize('#')[1].tokenize('=')[1]
    }, 
    secondTask: {
    subJob2 = build job: '../subUserJobs/Commit_loadTest',
                parameters: [
                        [$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
                        [$class: 'StringParameterValue', name: 'buildTrigger', value: buildTrigger ],
                        [$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL ],
                ];
	//returned values
    loadTestCommitID = subJob2.description.tokenize('#')[2].tokenize('=')[1]
    })

stage "AcceptanceStage"
/*job2 = build  job: '../subUserJobs/Acceptance_functionalTest',
            parameters: [
					[$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
					[$class: 'StringParameterValue', name: 'amiID', value: amiID ],
					[$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL ],
                ];*/
sleep(10)


stage "CapacityStage"
job2 = build  job: '../subUserJobs/Capacity_loadTest',
            parameters: [
					[$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
					[$class: 'StringParameterValue', name: 'amiID', value: amiID ],
					[$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL ],
                ];



stage "IntegrationStage"
job3 = build  job: '../subUserJobs/Integration',
            parameters: [
					[$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
					[$class: 'StringParameterValue', name: 'amiID', value: amiID ],
					[$class: 'StringParameterValue', name: 'serviceConfigBaseURL', value: serviceConfigBaseURL ],
                ];
                
                
                
stage "ReleaseStage"
sleep(10)
