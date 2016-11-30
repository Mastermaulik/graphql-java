package graphql.validation.rules;


import graphql.language.Value;
import graphql.schema.*;

import java.util.List;

public class VariablesTypesMatcher {

    public boolean doesVariableTypesMatch(GraphQLType variableType, Value variableDefaultValue, GraphQLType expectedType) {
        return checkType(effectiveType(variableType, variableDefaultValue), expectedType);
    }

    private GraphQLType effectiveType(GraphQLType variableType, Value defaultValue) {
        if (defaultValue == null) return variableType;
        if (variableType instanceof GraphQLNonNull) return variableType;
        return new GraphQLNonNull(variableType);
    }

    private boolean checkType(GraphQLType actualType, GraphQLType expectedType) {

        if((actualType instanceof GraphQLEnumType) && (expectedType instanceof GraphQLEnumType)) {
            GraphQLEnumType actualEnum = (GraphQLEnumType) actualType;
            GraphQLEnumType expectedEnum = (GraphQLEnumType) expectedType;

            if(actualEnum.getName().equals(expectedEnum.getName())) {
                List<GraphQLEnumValueDefinition> actualList = actualEnum.getValues();
                List<GraphQLEnumValueDefinition> expectedList = expectedEnum.getValues();

                if(actualList.size() == expectedList.size()) {
                    for(int i=0;i<actualList.size();i++) {
                        if(!actualList.get(i).getName().equals(expectedList.get(i).getName())) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            else {
                return false;
            }
        }

        if (expectedType instanceof GraphQLNonNull) {
            if (actualType instanceof GraphQLNonNull) {
                return checkType(((GraphQLNonNull) actualType).getWrappedType(), ((GraphQLNonNull) expectedType).getWrappedType());
            }
            return false;
        }

        if (actualType instanceof GraphQLNonNull) {
            return checkType(((GraphQLNonNull) actualType).getWrappedType(), expectedType);
        }



        if ((actualType instanceof GraphQLList) && (expectedType instanceof GraphQLList)) {
            return checkType(((GraphQLList) actualType).getWrappedType(), ((GraphQLList) expectedType).getWrappedType());
        }

        return actualType == expectedType;
    }

}
