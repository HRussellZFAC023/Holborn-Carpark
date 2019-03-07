Note - ro run the server (you may need admin privileges) do:`$ npm start` while within the server directory

Website
--------
www.holborncarpark.com

Documentations
--------------
- [Assignment details (what we need to have)](https://drive.google.com/open?id=1B0uanI_a_d9wvjlDerG6kn7LTfcYqbw2)

- [Initial discussion with Holborn](https://drive.google.com/open?id=1SKLDoqnbvnZxvsjOqNzOq1PjUkX-dWRz)

- [Requirements](https://drive.google.com/open?id=1UE5TXIrp9WK-IEVEzV0EYQYNr6p3yvSz)

- [Design](https://drive.google.com/open?id=1P8Z27j32Phr6LnlBHQ7ZJJdhj6QmMQ0S)

- [Group report writeup](https://docs.google.com/document/d/1Vk6iX0BysQidZeoMgF_f7MXtQpuimcAJj-XgpePxWF0/edit?usp=sharing)

The Holy Grail
--------------
1. Create a new branch and switch to it.

`$ git checkout -b [name_of_branch]`

2. Code, make changes, etc
- Stage all files.

`$ git add .`

- create a commit (make as many of those as possible), if there are conflicts follow on-screen instructions or ask me.

`$ git commit -m "[name_of_commit]"`

3. Rebase your changes ONTO master branch.

`$ git pull --rebase origin master`

4. Switch to master branch.

`$ git checkout master`

5. Merge your branch onto master.

`$ git merge [name_of_branch]`

6. delete your branch.

`$ git branch -d [name_of_branch]`

7. push branch to remote master

`$ git push`
