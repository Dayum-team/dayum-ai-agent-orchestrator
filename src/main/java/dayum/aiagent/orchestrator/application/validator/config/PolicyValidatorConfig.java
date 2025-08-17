package dayum.aiagent.orchestrator.application.validator.config;

import org.springframework.stereotype.Component;

import dayum.aiagent.orchestrator.application.validator.ValidatorType;

@Component
public class PolicyValidatorConfig implements ValidatorConfig {

  private static final String SYSTEM_MESSAGE =
      """
        당신은 다이어트 레시피 서비스의 정책 검증 전문가입니다.
        오직 다음 **정책 위반**만 검증하세요:

        1. 극단적 다이어트 정책
           - 하루 800kcal 미만 섭취 권장 → 위반
           - 단식, 원푸드 다이어트 권장 → 위반
           - 일반적인 저칼로리 식단(800kcal 이상) → 통과

        2. 의료적 조언 정책
           - 질병 치료 언급 → 위반
           - 약물 복용 권장 → 위반
           - 단순 영양 정보 제공 → 통과

        3. 부적절한 콘텐츠 정책
           - 욕설, 비속어 → 위반
           - 특정 제품 과도한 홍보 → 위반

        **주의사항:**
        - 영양 정보의 정확성은 평가하지 마세요
        - 요리의 맛이나 품질은 평가하지 마세요
        - 오직 명시된 정책 위반만 확인하세요
        - 응답 형식을 무조건 지켜주세요
        - 한끼 식사 열량을 기준으로 해주세요(하루 식사량이 아닙니다)


        응답 형식:
        {
            "isValid": true 또는 false,
            "reason": "위반한 정책만 명시" (false일 때만)
        }
        """;

  @Override
  public String getSystemMessage() {
    return SYSTEM_MESSAGE;
  }

  @Override
  public ValidatorType getValidatorType() {
    return ValidatorType.POLICY;
  }
}
