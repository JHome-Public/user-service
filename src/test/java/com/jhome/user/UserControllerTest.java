package com.jhome.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhome.user.exception.CustomException;
import com.jhome.user.response.ApiResponseCode;
import com.jhome.user.controller.UserUri;
import com.jhome.user.domain.UserStatus;
import com.jhome.user.dto.EditRequest;
import com.jhome.user.dto.JoinRequest;
import com.jhome.user.dto.UserResponse;
import com.jhome.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(RestDocumentationExtension.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    static final class DocumentName {
        public static final String JOIN_SUCCESS = "join-success";
        public static final String JOIN_FAIL_ARGS = "join-fail-args-invalid";
        public static final String JOIN_FAIL_ALREADY = "join-fail-already-exist";
        public static final String LIST_SUCCESS = "get-list-success";
        public static final String DETAIL_SUCCESS = "get-detail-success";
        public static final String DETAIL_FAIL = "get-detail-fail-not-found";
        public static final String EDIT_SUCCESS = "edit-success";
        public static final String LEAVE_SUCCESS = "leave-success";
    }

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    @Test
    void testJoin_Success() throws Exception {
        // given
        final JoinRequest request = getJoinRequest();
        final String requestJson = objectMapper.writeValueAsString(request);

        final UserResponse response = getTestResponse();
        when(userService.addUser(any(JoinRequest.class))).thenReturn(response);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post(UserUri.DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ApiResponseCode.SUCCESS.getCode()))
                .andDo(print())
                .andDo(document(DocumentName.JOIN_SUCCESS,
                        requestFields(getJoinRequestFields()),
                        responseFields(getUserResponseFields())
                ));
    }

    @Test
    void testJoin_RequestArgsNotValid() throws Exception {
        // given
        final JoinRequest invalidRequest = JoinRequest.builder()
                .username("a")  // 최소 길이(2) 미달
                .password("1234567")  // 최소 길이(8) 미달 + 패턴 불일치
                .email("invalid-email") // 이메일 형식 오류
                .name("") // NotBlank 위반
                .userType(0) // 범위 벗어남
                .build();

        final String requestJson = objectMapper.writeValueAsString(invalidRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post(UserUri.DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(ApiResponseCode.REQUEST_ARGS_INVALID.getCode()))
                .andExpect(jsonPath("$.data")
                        .isArray())
                .andExpect(jsonPath("$.data.length()")
                        .value(6))
                .andDo(print())
                .andDo(document(DocumentName.JOIN_FAIL_ARGS,
                        requestFields(getJoinRequestFields()),
                        responseFields(getResponseFieldsNoData())
                ));
    }

    @Test
    void testJoin_UserAlreadyExist() throws Exception {
        // given
        final JoinRequest request = getJoinRequest();
        final String requestJson = objectMapper.writeValueAsString(request);

        when(userService.addUser(any(JoinRequest.class))).thenThrow(new CustomException(ApiResponseCode.USER_ALREADY_EXIST));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post(UserUri.DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(ApiResponseCode.USER_ALREADY_EXIST.getCode()))
                .andDo(print())
                .andDo(document(DocumentName.JOIN_FAIL_ALREADY,
                        requestFields(getJoinRequestFields()),
                        responseFields(getResponseFieldsNoData())
                ));
    }

    @Test
    void testList_Success() throws Exception {
        // given
        final List<UserResponse> userResponseList = List.of(
                getTestResponse(),
                getTestResponse()
        );
        final Pageable pageable = Pageable.ofSize(10);
        final Page<UserResponse> userResPage = new PageImpl<>(userResponseList, pageable, userResponseList.size());
        when(userService.getUserResponsePage(anyString(), any(Pageable.class))).thenReturn(userResPage);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get(UserUri.DEFAULT)
                        .params(getSearchParams()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ApiResponseCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.numberOfElements")
                        .value(2))
                .andDo(print())
                .andDo(document(DocumentName.LIST_SUCCESS,
                        queryParameters(getSearchQueryParams()),
                        responseFields(getUserResponseListFields())
                ));
    }

    @Test
    void testDetail_Success() throws Exception {
        // given
        final UserResponse userResponse = getTestResponse();
        when(userService.getUserResponse(anyString())).thenReturn(userResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get(UserUri.DEFAULT + UserUri.USERNAME_VARIABLE, "username"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ApiResponseCode.SUCCESS.getCode()))
                .andDo(print())
                .andDo(document(DocumentName.DETAIL_SUCCESS,
                        pathParameters(getUsernameParameter()),
                        responseFields(getUserResponseFields())
                ));
    }

    @Test
    void testDetail_UserNotFound() throws Exception {
        // given
        when(userService.getUserResponse(anyString())).thenThrow(new CustomException(ApiResponseCode.USER_NOT_FOUND));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get(UserUri.DEFAULT + UserUri.USERNAME_VARIABLE, "username"));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(ApiResponseCode.USER_NOT_FOUND.getCode()))
                .andDo(print())
                .andDo(document(DocumentName.DETAIL_FAIL,
                        pathParameters(getUsernameParameter()),
                        responseFields(getResponseFieldsNoData())
                ));
    }

    @Test
    void testEdit_Success() throws Exception {
        // given
        final EditRequest request = getEditRequest();
        final String requestJson = objectMapper.writeValueAsString(request);

        final UserResponse userResponse = getTestResponse();
        when(userService.editUser(anyString(), any(EditRequest.class))).thenReturn(userResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .put(UserUri.DEFAULT + UserUri.USERNAME_VARIABLE, "username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code")
                        .value(ApiResponseCode.SUCCESS.getCode()))
                .andDo(print())
                .andDo(document(DocumentName.EDIT_SUCCESS,
                        pathParameters(getUsernameParameter()),
                        requestFields(getEditReqyestFields()),
                        responseFields(getUserResponseFields())
                ));
    }

    @Test
    void testLeave_Success() throws Exception {
        // given

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .delete(UserUri.DEFAULT + UserUri.USERNAME_VARIABLE, "username"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ApiResponseCode.SUCCESS.getCode()))
                .andDo(print())
                .andDo(document(DocumentName.LEAVE_SUCCESS,
                        pathParameters(getUsernameParameter()),
                        responseFields(getResponseFieldsNoData())
                ));
    }

    private JoinRequest getJoinRequest() {
        return JoinRequest.builder()
                .username("jhome")
                .password("password123!")
                .name("jhome")
                .email("email@jhome.com")
                .userType(1)
                .build();
    }

    private EditRequest getEditRequest() {
        return EditRequest.builder()
                .password("changed123!")
                .email("email@jhome.com")
                .build();
    }

    private UserResponse getTestResponse() {
        return UserResponse.builder()
                .username("jhome")
                .role("ROLE_USER")
                .type("JHOME")
                .name("jhome")
                .email("email@jhome.com")
                .phone("01012345678")
                .picture("/path/to/picture")
                .createAt("yyyy-MM-dd HH:mm:ss")
                .updateAt("yyyy-MM-dd HH:mm:ss")
                .status(UserStatus.ACTIVE.name())
                .build();
    }

    private ParameterDescriptor getUsernameParameter() {
        return parameterWithName("username").description("사용자 ID");
    }

    private MultiValueMap<String, String> getSearchParams() {
        return new LinkedMultiValueMap<>(Map.of(
                "page", List.of("0"),
                "size", List.of("10"),
                "sortBy", List.of("createdAt"),
                "sortDirection", List.of("desc"),
                "searchKeyword", List.of("keyword")
        ));
    }

    private List<ParameterDescriptor> getSearchQueryParams() {
        return List.of(
                parameterWithName("page").description("페이지 번호"),
                parameterWithName("size").description("페이지 내 컨텐츠 개수"),
                parameterWithName("sortBy").description("정렬 기준"),
                parameterWithName("sortDirection").description("정렬 방향"),
                parameterWithName("searchKeyword").description("검색어")
        );
    }

    private List<FieldDescriptor> getEditReqyestFields() {
        return List.of(
                fieldWithPath("password").description("변경 비밀번호"),
                fieldWithPath("email").description("변경 이메일")
        );
    }

    private List<FieldDescriptor> getJoinRequestFields() {
        return List.of(
                fieldWithPath("username").description("사용자 ID"),
                fieldWithPath("password").description("비밀번호"),
                fieldWithPath("name").description("이름"),
                fieldWithPath("email").description("이메일"),
                fieldWithPath("userType").description("사용자 구분")
        );
    }

    private List<FieldDescriptor> getUserResponseFields() {
        return List.of(
                fieldWithPath("code").description("응답 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data").description("데이터"),
                fieldWithPath("data.username").description("사용자 이름"),
                fieldWithPath("data.name").description("사용자 이름"),
                fieldWithPath("data.email").description("사용자 이메일"),
                fieldWithPath("data.role").description("사용자 역할"),
                fieldWithPath("data.type").description("사용자 타입"),
                fieldWithPath("data.phone").description("사용자 전화번호"),
                fieldWithPath("data.picture").description("사용자 사진"),
                fieldWithPath("data.createAt").description("사용자 생성 시간"),
                fieldWithPath("data.updateAt").description("사용자 수정 시간"),
                fieldWithPath("data.status").description("사용자 활성화 상태")
        );
    }

    private List<FieldDescriptor> getUserResponseListFields() {
        return List.of(
                fieldWithPath("code").description("응답 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data").description("데이터"),

                fieldWithPath("data.content.[].username").description("사용자 이름"),
                fieldWithPath("data.content.[].name").description("사용자 이름"),
                fieldWithPath("data.content.[].email").description("사용자 이메일"),
                fieldWithPath("data.content.[].role").description("사용자 역할"),
                fieldWithPath("data.content.[].type").description("사용자 타입"),
                fieldWithPath("data.content.[].phone").description("사용자 전화번호"),
                fieldWithPath("data.content.[].picture").description("사용자 사진"),
                fieldWithPath("data.content.[].createAt").description("사용자 생성 시간"),
                fieldWithPath("data.content.[].updateAt").description("사용자 수정 시간"),
                fieldWithPath("data.content.[].status").description("사용자 활성화 상태"),

                fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                fieldWithPath("data.pageable.sort.empty").description("정렬 정보가 비어 있는지 여부"),
                fieldWithPath("data.pageable.sort.sorted").description("정렬이 적용되었는지 여부"),
                fieldWithPath("data.pageable.sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                fieldWithPath("data.pageable.offset").description("페이지 오프셋"),
                fieldWithPath("data.pageable.paged").description("페이징 여부"),
                fieldWithPath("data.pageable.unpaged").description("페이징이 적용되지 않았는지 여부"),

                fieldWithPath("data.last").description("마지막 페이지 여부"),
                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                fieldWithPath("data.totalElements").description("전체 요소 수"),
                fieldWithPath("data.first").description("첫 페이지 여부"),
                fieldWithPath("data.size").description("현재 페이지 크기"),
                fieldWithPath("data.number").description("현재 페이지 번호"),

                fieldWithPath("data.sort.empty").description("정렬 정보가 비어 있는지 여부"),
                fieldWithPath("data.sort.sorted").description("정렬이 적용되었는지 여부"),
                fieldWithPath("data.sort.unsorted").description("정렬이 적용되지 않았는지 여부"),

                fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 개수"),
                fieldWithPath("data.empty").description("데이터가 비어 있는지 여부")
        );
    }

    private List<FieldDescriptor> getResponseFieldsNoData() {
        return List.of(
                fieldWithPath("code").description("응답(에러) 코드"),
                fieldWithPath("message").description("응답(에러) 메시지"),
                fieldWithPath("data").description("데이터")
        );
    }
}
