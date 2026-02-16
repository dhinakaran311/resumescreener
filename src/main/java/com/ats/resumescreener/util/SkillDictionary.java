package com.ats.resumescreener.util;

import java.util.*;

public class SkillDictionary {

    public static final Map<String, Set<String>> SKILL_MAP = Map.of(

        "PROGRAMMING", Set.of(
                "java","python","c","cpp","javascript",
                "typescript","go","rust","kotlin"
        ),

        "BACKEND", Set.of(
                "spring","springboot","hibernate",
                "node","express","django","flask",
                "fastapi","microservices","rest"
        ),

        "FRONTEND", Set.of(
                "html","css","react","angular",
                "vue","nextjs","tailwind"
        ),

        "DATABASE", Set.of(
                "sql","mysql","postgresql",
                "mongodb","oracle","redis",
                "firebase","cassandra"
        ),

        "ML_AI", Set.of(
                "machinelearning","machine","learning",
                "deeplearning","nlp","tensorflow",
                "pytorch","scikitlearn","opencv",
                "transformers","bert"
        ),

        "DATA_SCIENCE", Set.of(
                "pandas","numpy","matplotlib",
                "seaborn","featureengineering",
                "dataanalysis"
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
