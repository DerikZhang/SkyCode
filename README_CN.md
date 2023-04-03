# SkyCode

### 默认服务器入参json
```json
{
  "action": "next",
  "messages": [
    {
      "id": "aa8cc766-1df5-4da1-8f74-6b58920f69ec",
      "role": "user",
      "content": {
        "content_type": "text",
        "parts": [
          "test data"
        ]
      },
      "author": {
        "role": "user"
      }
    }
  ],
  "parent_message_id": "42d95668-b396-4337-b50a-069ac6716515",
  "model": "text-davinci-002-render-sha"
}
```

### 默认服务器出参json
```json
{
  "conversation_id": "42d95668-b396-4337-b50a-069ac6716514",
  "message": {
    "id": "42d95668-b396-4337-b50a-069ac6716517",
    "content": {
      "parts": ["test data"]
    }
  }
}
```