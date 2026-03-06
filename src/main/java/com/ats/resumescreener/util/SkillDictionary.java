package com.ats.resumescreener.util;

import java.util.*;

public class SkillDictionary {

    public static final Map<String, Set<String>> SKILL_MAP = Map.of(

        "PROGRAMMING", Set.of(
                "java","python","c","cpp","javascript",
                "typescript","go","rust","kotlin","js","ts"
        ),

        "BACKEND", Set.of(
                "spring","springboot","hibernate",
                "node","express","django","flask",
                "fastapi","microservices","rest","restful","api","apis"
        ),

        "FRONTEND", Set.of(
                "html","css","react","angular",
                "vue","nextjs","tailwind","javascript","typescript"
        ),

        "DATABASE", Set.of(
                "sql","mysql","postgresql",
                "mongodb","oracle","redis",
                "firebase","cassandra","nosql"
        ),

        "ML_AI", Set.of(
                "machinelearning","machine","learning","ml","ai",
                "deeplearning","nlp","tensorflow",
                "pytorch","scikitlearn","opencv","sklearn",
                "transformers","bert","artificial","intelligence"
        ),

        "DATA_SCIENCE", Set.of(
                "pandas","numpy","matplotlib",
                "seaborn","featureengineering",
                "dataanalysis","datascience","analytics"
        ),

        "CLOUD", Set.of(
                "aws","azure","gcp",
                "serverless","cloud"
        ),

        "DEVOPS", Set.of(
                "docker","kubernetes",
                "jenkins","terraform",
                "githubactions","ci","cd"
        ),

        "TOOLS", Set.of(
                "git","github","gitlab",
                "jira","postman","linux"
        )
    );
}
