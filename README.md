# msg-encrypted-app
Encrypted messaging java application with a custom PKI using a non-relational database to store users, messages, encrypted private keys and digital certificates; and different ways to encrypt messages (asymmetrically, symmetrically, digital envelope, plain text, digitally signed messages).

### Screenshots
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/ad38a3a9-85a6-4721-87ec-5d8c046ac077)
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/fc855eb0-e884-4766-96e0-736fc3474c8e)
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/0d170cc6-6dae-4979-8216-c74d4ab95707)
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/25dbd32b-a550-4982-99df-8ef3d21aa56b)
If someone managed to get into Fulanito's messages without his pass phrase (used to decrypt their provate key and decrypt the recieved messages), left chat is the impostor and right chat the legit one. Messages in green were sent in plain text (anyone can see them) and pink message is asymetrcially encrypted (only Fulanito with his private key can decrypt it)
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/33113435-1c12-4ae4-9525-6aaa66d98e8b)


### Database
Registering Agency (Digital certificates collection)
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/4d65eb9c-db56-4c38-aeff-17c45b9a061e)
Chats collection
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/4cc33d14-f308-4331-b9f7-e729ce53bb7c)
Encrypted private keys colelction
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/93d6ccf2-7f5b-4afd-9255-e7c1a09ce5e1)
Users' collection
![image](https://github.com/fdezSeb01/msg-encrypted-app/assets/110956552/6fb51e07-232f-471f-bb67-9a71d738921e)
