# CS1813_2019_05

#The Holy Grail

$ git checkout -b [name_of_branch]           //create a new branch and switch to it


 --Code, make changes, etc          


$ git add .			                    //stage all file	                      

//create a commit (make as many of those as possible)
//if there are conflicts follow on-screen instructions or ask me
$ git commit -m "[name_of_commit]"        

$ git rebase master 			     //rebase your changes ONTO master branch

$ git checkout master 		 	     //switch to master branch

$ git merge [name_of_branch]		     //merge your branch onto master

$ git branch -d [name_of_branch]	     //delete your branch



If you want to delete already existing folders open up git bash and do:

$ git rm -r --cached .

$ git add .

$ git commit -am "Reason for commit"
