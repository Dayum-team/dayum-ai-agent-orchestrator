package dayum.aiagent.orchestrator.application.validator.config;

import org.springframework.stereotype.Component;

import dayum.aiagent.orchestrator.application.validator.ValidatorType;

@Component
public class SelfCheckValidatorConfig implements ValidatorConfig {

  private static final String SYSTEM_MESSAGE =
      """
        당신은 다이어트 레시피 서비스의 일관성 검증 전문가입니다.
        오직 다음 **논리적 일관성**만 검증하세요:

        1. 제목과 내용의 일치
           - 제목: "채식 레시피" → 내용: "고기 요리" → 불일치
           - 제목: "닭가슴살 요리" → 내용: "닭가슴살 레시피" → 일치

        2. 메시지와 QuickReply의 관련성
           - 레시피 추천 메시지 → QuickReply: PICK_THIS → 일치
           - 재료 등록 메시지 → QuickReply: PICK_THIS → 불일치

        3. 내부 모순
           - 같은 재료에 다른 칼로리 표기 → 모순
           - 처음엔 "매운맛", 나중엔 "순한맛" → 모순

        **주의사항:**
        - 건강이나 다이어트 적합성은 평가하지 마세요
        - 극단적인지는 평가하지 마세요
        - 오직 논리적 일관성만 확인하세요
        - 응답 형식을 무조건 지켜주세요
        - 한끼 식사 열량을 기준으로 해주세요(하루 식사량이 아닙니다)
        
        응답 형식:
        {
            "isValid": true 또는 false,
            "reason": "논리적 모순만 작성" (false일 때만)
        }
        """;

  @Override
  public String getSystemMessage() {
    return SYSTEM_MESSAGE;
  }

  @Override
  public ValidatorType getValidatorType() {
    return ValidatorType.SELF_CHECK;
  }
}
