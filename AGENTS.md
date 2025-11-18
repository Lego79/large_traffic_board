# Repository Guidelines

## 프로젝트 목적
본 프로젝트의 목표는 대규모 트래픽을 견딜 수 있는 게시판을 구축하는 것이며, Controller-서비스-DB 계층을 명확히 분리해 Spring 기반 API 경계를 보호합니다. 백엔드는 "나를 호출하면 이 데이터를 보내줄게"라는 계약을 제공하고, 컨트롤러는 DTO 중심 검증으로 무분별한 문자열 전달을 막은 뒤 서비스 계층에 넘깁니다. 서비스는 변환기/Converter를 이용해 비즈니스 로직을 정제하고, 레포지토리는 신뢰 가능한 입력만 처리합니다. 외부에서 내부로 들어오는 경계는 오류·예외를 공격적으로 차단해야 하며, 내부에서는 검증이 끝났다는 가정 아래 언어 레벨 최적화(3rd perspective)까지 고려합니다. Spring, Docker, AWS, Kafka, Kubernetes, 서비스/테스트 코드, 작업 순서 기록이 이러한 방어선과 확장성을 기술적으로 뒷받침합니다.

## Project Structure & Module Organization
- src/main/java/com/megaboard/project는 엔트리포인트를 포함하며 controller, service, domain, 
epository, support/converter 패키지로 확장하세요.
- 구성/템플릿/정적 자산은 src/main/resources에 두고 pplication-<profile>.properties로 프로필별 설정을 유지합니다.
- 테스트는 src/test/java/com/megaboard/project에서 메인 트리를 미러링하여 각 공개 컴포넌트마다 짝이 되게 합니다.

## Build, Test, and Development Commands
- ./gradlew bootRun — 포트 8080에서 로컬 설정으로 애플리케이션 기동.
- ./gradlew test — JUnit 5 + Spring Boot Test 실행; --info로 상세 추적 출력.
- ./gradlew build — 컴파일·테스트 후 uild/libs/*.jar를 생성하며 CI 기준 명령입니다.

## Coding Style & Naming Conventions
- Java 17, 4-space 들여쓰기, UTF-8, 120자 내외 라인 길이를 유지합니다.
- 클래스는 PascalCase, 메서드/필드는 camelCase, 상수는 UPPER_SNAKE를 사용합니다.
- @RestController, @Service, @Repository 등 스테레오타입과 Lombok 어노테이션을 선언부 상단에 모으고, 변환 로직은 DTO/Converter 클래스로 캡슐화합니다.

## Testing Guidelines
- 테스트 클래스는 *Tests 접미사(BoardServiceTests 등)를 사용하고 @DataJpaTest, @WebMvcTest 등 슬라이스 테스트를 우선시합니다.
- 컨트롤러 경계의 방어 로직, 서비스 변환, 레포지토리 쿼리를 모두 ./gradlew test로 커버하고 새 코드 기준 70% 이상 커버리지를 목표로 합니다.

## Commit & Pull Request Guidelines
- Conventional Commits(eat:, ix:, chore:) 형식을 따르고 이슈 ID를 본문이나 제목에 언급하세요.
- 커밋 본문은 72자에서 줄바꿈하고, 깨지는 변경은 BREAKING CHANGE: 푸터에 기록하며 PR에는 범위·테스트·API/스크린샷 근거를 포함합니다.

## Configuration & Security Tips
- 비밀 값은 환경 변수나 Git에 추적되지 않는 pplication-*.properties에서 주입하고, 저장소에는 커밋하지 않습니다.
- --spring.profiles.active=local|stage|prod로 환경을 분리하고 Docker/AWS/Kafka/Kubernetes 설정을 수정할 때마다 PR에 작업 순서와 검증 로그를 남기세요.
## Board DB DDL

-- Schema name
mega_board

--db - mysql
name: root
password: mysql
-- 사용자 테이블
CREATE TABLE `user` (
`id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
`username` VARCHAR(100) NOT NULL,
`email` VARCHAR(255) NOT NULL,
`password_hash` VARCHAR(255) NOT NULL,
`use_yn` CHAR(1) NOT NULL DEFAULT 'Y',    -- 사용 여부 (Y/N)
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
UNIQUE KEY `uq_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 게시판 테이블
CREATE TABLE `board` (
`id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
`user_id` BIGINT UNSIGNED NOT NULL,        -- 작성자
`title` VARCHAR(200) NOT NULL,
`content` TEXT NOT NULL,
`use_yn` CHAR(1) NOT NULL DEFAULT 'Y',
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_board_user_id` (`user_id`),
CONSTRAINT `fk_board_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 댓글 테이블
CREATE TABLE `comment` (
`id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
`board_id` BIGINT UNSIGNED NOT NULL,       -- 어떤 게시물에 대한 댓글
`user_id` BIGINT UNSIGNED NOT NULL,        -- 댓글 작성자
`content` TEXT NOT NULL,
`use_yn` CHAR(1) NOT NULL DEFAULT 'Y',
`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_comment_board_id` (`board_id`),
KEY `idx_comment_user_id` (`user_id`),
CONSTRAINT `fk_comment_board` FOREIGN KEY (`board_id`) REFERENCES `board`(`id`)
ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
