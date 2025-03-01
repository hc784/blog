# 📌 블로그 개인 프로젝트

## 📝 프로젝트 소개
Spring Boot, JPA, Thymeleaf 기반의 웹 애플리케이션으로, 회원가입한 유저가 자신의 블로그를 생성하고 관리할 수 있는 기능을 제공합니다.

## 📅 개발 기간
**2024.01.18 ~ 2024.02.28**

## 🛠 기술 스택
- **Backend:** Spring Boot, Spring Data JPA, Spring Security  
- **Frontend:** Thymeleaf, BootStrap, HTML, CSS, JavaScript 
- **Database:** MySQL (AWS RDS)  
- **Build Tool:** Gradle  
- **Deployment:** AWS (EC2, S3, CodeDeploy), Github Action  

## ✨ 주요 기능
- **회원가입 및 로그인**  
  - Spring Security 및 Session 기반 회원가입, 로그인 기능
  - OAuth 로그인 (네이버, 카카오, 구글)

- **게시물 관리**  
  - 게시물 CRUD 기능 제공
  - CKEditor를 활용한 게시글 작성
  - 게시글의 이미지를 S3에 저장하고 presigned URL을 통해 불러오기

- **댓글 기능**  
  - 댓글 CRUD
  - 비로그인 사용자도 댓글 작성 가능 (작성자명, 비밀번호 입력 필요)

- **블로그 관리**  
  - 사용자별 블로그 생성 및 관리
  - 블로그 카테고리 설정
  - 프로필 설정 기능

- **검색 기능**  
  - 게시물 제목 및 콘텐츠를 포함한 검색 기능

- **CI/CD 적용**  
  - AWS EC2, S3, CodeDeploy, Github Action을 사용한 자동 배포 적용

---

## 📷 실행 화면
### 🔹 전체 블로그 목록 페이지
![Image](https://github.com/user-attachments/assets/f75f96f1-179f-4f24-a331-d8a2afe96d2f)
- 회원가입 시 자동으로 블로그가 생성되며, 해당 페이지에 노출됨.

### 🔹 게시글 목록 페이지
![Image](https://github.com/user-attachments/assets/add6a587-a790-4d78-9b61-190a00c0b308)
- **JPA Page**를 사용한 페이징 처리
- 블로그 주인이 아닐 경우, 글쓰기 및 블로그 관리 기능 접근 제한
- 게시글 콘텐츠를 HTML로 저장하여, 목록 페이지에서는 서버에서 HTML 태그를 제거한 프리뷰 제공

### 🔹 게시물 검색 기능
![Image](https://github.com/user-attachments/assets/2abd7ab4-a027-4f83-a08b-d7263455ae60)
- 게시물의 **제목** 및 **내용**에서 검색 가능

### 🔹 게시글 상세 페이지
![Image](https://github.com/user-attachments/assets/06e9214d-f732-4db1-a6fe-89bdde1bbdd3)
- REST API를 활용하여 **JS로 비동기 처리** (댓글 기능)
- 비로그인 사용자도 댓글 작성 가능 (작성자명 및 비밀번호 입력 필요)

### 🔹 글쓰기 기능
![Image](https://github.com/user-attachments/assets/e6b0c1c6-eb39-477b-96a7-7f3b42134964)
- **CKEditor**를 사용하여 HTML 형식으로 변환 후 데이터베이스에 저장
- 글쓰기 페이지에 들어오면 **임시 게시글**이 자동 저장됨
- **이미지 업로드 시 즉시 S3에 저장**
- 게시글 작성을 중단하면 **임시 게시글 및 S3에 저장된 이미지 삭제**
- 보안을 위해 presigned URL을 활용하여 게시글 조회 시마다 새로운 presigned URL 발급

### 🔹 카테고리 설정 페이지
![Image](https://github.com/user-attachments/assets/e4912d17-6234-45d0-ba2b-f5e1ad2b0124)
- **JS Drag & Drop**을 사용하여 카테고리 계층 구조 설정 가능
- 자식 카테고리를 최상단으로 이동 시 오류 메시지 발생 및 저장 불가
- REST API를 통해 서버에서 카테고리 목록을 불러오고, 변경 사항을 서버에 저장
- 변경 사항 저장 시, **JSON 데이터**로 전달 후 order 컬럼을 기준으로 정렬

### 🔹 프로필 설정 페이지
![Image](https://github.com/user-attachments/assets/d5946ed4-e79a-4dec-8b48-a8a7c3996916)
- 프로필 사진을 **서버의 로컬 폴더**에 저장

### 🔹 로그인/회원가입 페이지
![Image](https://github.com/user-attachments/assets/abb60fb4-4c02-440e-b12b-780dd2830da8)
![Image](https://github.com/user-attachments/assets/a921c445-b0f0-4224-91e7-7149272b18c3)
- **MDB BootStrap 템플릿** 사용
- **OAuth 로그인 및 일반 로그인 기능 제공**

---

## 🚀 프로젝트 배포
- **AWS EC2 + S3 + CodeDeploy + Github Action**을 활용한 자동 배포 구성
- CI/CD 파이프라인을 구축하여 코드 변경 사항이 반영될 때마다 자동 배포 수행

---
