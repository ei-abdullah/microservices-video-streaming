## Distributed Video Streaming Platform using Java/Spring Boot

### ENVs of modules

| S.no | module | env |
| --- | --- | --- |
| 1 | `media-service` | `SUPPORT_EMAIL`, `APP_PASSWORD`|
| 2 | `user-service` | `JWT_SECRET_KEY` |
| 3 | `auth-service` | `JWT_SECRET_KEY` |

### Port numbers
- 8080 - `api-gateway`
- 8761 - `naming-server`
- 8081 - `user-service`
- 8000 - `user-service`
- 8100 - `notification-service`
- 8200 - `auth-service`
- 8300 - `media-service`
- 8400 - `transcoding-service`

### Future
- Complete the envs e.g. `DATABASE_URL`, `RABBITMQ_HOST`, `RABBITMQ_PORT` etc.
- Add encoding and decoding of video
- Add config server