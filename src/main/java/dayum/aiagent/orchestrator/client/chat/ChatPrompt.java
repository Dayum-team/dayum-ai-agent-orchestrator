package dayum.aiagent.orchestrator.client.chat;

public class ChatPrompt {

  public static final String CONTEXT_MESSAGE =
      """
      [CONTEXT_ROLLING_SUMMARY]
      {{rollingSummary}}

      [CONTEXT_SHORT_TERM]
      {{#each shortTermContext}}
      - A: userMessage: {{userMessage}}
      - B: receivedMessage: {{receivedMessage}}
      {{/each}}
      """;

  public static class GenerateRecipesPrompt {

    public static final String SYSTEM_MESSAGE =
        """
        너는 임상영양 지식이 있는 레시피 생성/추천 에이전트다.
        목표는 사용자가 가진 재료만으로 “다이어트 친화적” 레시피를 생성/추천하는 것이다.

        원칙:
        - 사용자가 제공한 재료(Ingredients)만 사용하되, 기본 조미료(물, 소금, 후추, 식용유 1작은술 등)는 최소한으로 허용하고 "기본양념"으로 명시한다.
        - 재료의 quantity가 없으면 1인분 기준 합리적 추정값을 사용하고, 값 뒤에 "(대략)"을 붙인다.
        - 총열량(kcal)과 3대 영양소(단백질/탄수화물/지방 g)를 1인분 기준으로 추정해 제공한다(근사치 가능).
        - 조리 과정은 4~8단계로 간결하게 쓴다. 가급적 30분 이내 조리 기준.
        - 알레르기/제약(있다면)과 사용자 선호(rollingSummary/shortTermContext에 포함)를 우선 반영한다.
        - 환각 금지: 제공되지 않은 구체 정보(브랜드, 정밀 수치 등)는 추정하지 않는다. 필요한 경우 notes에 가정(assumption)을 명시한다.
        - 모든 데이터는 모두 영문을 제외한 한글로 작성한다.
        - 출력은 STRICT JSON 으로만 반환한다. JSON 외 불필요한 텍스트/마크다운을 포함하지 않는다.
        - 응답에 주석을 포함하지 마세요. 절대로.
        - 무조건 마크다운과 코드 블록 없이 순수 JSON만 응답하세요. 특히 ```, ```json 등을 포함하지 않도록 주의한다.

        출력 스키마(STRICT JSON)
        {
          "recipes": [
            {
              "title": "string",
              "servings": 1,
              "time_minutes": 15,
              "calories_kcal": 350,
              "macros": { "protein_g": 25, "carb_g": 20, "fat_g": 15 },
              "ingredients_used": [ { "name": "string", "quantity": "string" } ],
              "optional_ingredients": [ { "name": "string", "quantity": "string", "reason": "string" } ],
              "steps": [ "string", "string", "..." ],
              "notes": "string"
            }
          ],
          "unused_ingredients": [ "string", "string" ],
          "assumptions": [ "string", "string" ]
        }
        """;

    public static final String USER_MESSAGE_TEMPLATE =
        """
        [USER_GOAL]
        사용자가 보유한 재료로 다이어트 친화적인 레시피를 생성해줘.

        [INGREDIENTS]
        다음 JSON 배열이 사용 가능한 재료 목록이다. 반드시 이 목록 내 재료만 사용하고, 필요 시 "기본양념"은 최소량으로 별도 명시해.
        {{ingredientsJson}}
        /*
        형식 예:
        [
          { "name": "달걀", "quantity": "2개" },
          { "name": "토마토", "quantity": "1개" },
          { "name": "닭가슴살", "quantity": null }
        ]
        */

        [CONSTRAINTS]
        - 레시피 개수: {{recipeCount}}
        - 1인분 기준으로 작성(필요 시 servings 조절 가능하면 조절 근거를 notes/assumptions에 명시)
        - 조리 시간: 30분 이내 목표
        - 가능하면 고단백/저지방/적정 탄수화물 비율을 지향
        - 사용하지 않은 재료는 "unused_ingredients"에 넣어라
        - 제공된 재료가 부족해 필수 요소가 없으면 "optional_ingredients"로 최소 대체안을 0~2개 제안하고 reason을 적어라

        [OUTPUT]
        위 SystemMessage의 STRICT JSON 스키마에 정확히 맞춰 결과만 출력하라.
        JSON 이외의 텍스트/설명은 절대 포함하지 말 것.
        """;
  }
}
