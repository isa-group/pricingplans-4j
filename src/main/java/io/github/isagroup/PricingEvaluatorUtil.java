package io.github.isagroup;

import java.util.Map;
import java.util.logging.Logger;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Component;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.PlanContextManager;
import io.github.isagroup.services.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Utility class that provides methods to generate and manage JWT that contains the pricing plan evaluation context.
 */
@Component
public class PricingEvaluatorUtil {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PricingContext pricingContext;

    Logger logger = Logger.getLogger(PricingEvaluatorUtil.class.getName());

    /**
     * Generate a user authentication JWT that includes the pricing plan evaluation context.
     * This token is generated by using the information provided by the configured {@link PricingContext}
     * @return JWT that contains all the information
     */
    public String generateUserToken() {

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", pricingContext.getUserAuthorities());

        PlanContextManager planContextManager = new PlanContextManager();
        planContextManager.userContext = pricingContext.getUserContext();

        planContextManager.planContext = pricingContext.getPlanContext();

        Map<String, Feature> evaluationConext = pricingContext.getFeatures();

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        Map<String, Object> featureMap = new HashMap<>();

        Map<String, Object> featureStatus;

        for (String key : evaluationConext.keySet()) {

            featureStatus = new HashMap<>();

            String expression = evaluationConext.get(key).getExpression();

            if (!expression.trim().equals("")) {
                Boolean eval = parser.parseExpression(expression).getValue(context, planContextManager,
                        Boolean.class);

                featureStatus.put("eval", eval);

            }else{
                featureStatus.put("eval", false);
            }

            if (expression.contains("<") || expression.contains(">")) {

                String userContextStatusKey = expression.split("\\[[\\\"|']")[1].split("[\\\"|']\\]")[0].trim();

                featureStatus.put("used", planContextManager.userContext.get(userContextStatusKey));
                featureStatus.put("limit", planContextManager.planContext.get(key));
                
            }else{
                featureStatus.put("used", null);
                featureStatus.put("limit", null);
            }
            
            featureMap.put(key, featureStatus);
        }

        claims.put("features", featureMap);
        claims.put("userContext", planContextManager.userContext);
        claims.put("planContext", planContextManager.planContext);

        String subject = "Default";

        if (pricingContext.getUserContext().containsKey("username")) {
            subject = (String) pricingContext.getUserContext().get("username");
        }else if (pricingContext.getUserContext().containsKey("user")) {
            subject = (String) pricingContext.getUserContext().get("user");
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + pricingContext.getJwtExpiration()))
                .signWith(SignatureAlgorithm.HS512, pricingContext.getJwtSecret())
                .compact();
    }

    /**
     * Modifies the given JWT by changing the evaluation of the given feature by a {@link String} expression that will be evaluated on the client side of the application.
     * @param token generated JWT returned by {@link PricingEvaluatorUtil#generateUserToken()} method
     * @param featureId the id of a feature that is defined inside the token body
     * @param expression the expression of the feature that will replace its evaluation
     * @return Modified version of the provided JWT that contains the new expression in the "eval" attribute of the feature.
     */
    public String addExpressionToToken(String token, String featureId, String expression) {

        Map<String, Map<String, Object>> features = jwtUtils.getFeaturesFromJwtToken(token);
        String subject = jwtUtils.getSubjectFromJwtToken(token);

        try{
            Map<String, Object> feature = (Map<String, Object>) features.get(featureId);
            feature.put("eval", expression);
        }catch(Exception e){
            logger.warning("Feature not found");
        }

        return buildJwtToken(features, subject);
    }

    private String buildJwtToken(Map<String, Map<String, Object>> features, String subject) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("authorities", pricingContext.getUserAuthorities());
        claims.put("features", features);
        claims.put("userContext", pricingContext.getUserContext());
        claims.put("planContext", pricingContext.getPlanContext());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + pricingContext.getJwtExpiration()))
                .signWith(SignatureAlgorithm.HS512, pricingContext.getJwtSecret())
                .compact();
    }

}
