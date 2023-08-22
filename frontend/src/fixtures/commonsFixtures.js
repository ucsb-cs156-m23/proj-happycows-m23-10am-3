const commonsFixtures = {
    threeCommons: [
        {
            "id": 5,
            "name": "Seths Common",
            "day": 5,
            "startingDate": "2023-08-20",
            "endingDate": "2023-09-20",
            "startingBalance": 1200.10,
            "totalPlayers": 50,
            "cowPrice": 15,
            "milkPrice": 10,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Noop",
            "aboveCapacityHealthUpdateStrategy": "Noop"
        },
        {
            "id": 4,
            "name": "Kevin's Commons",
            "day": 5,
            "startingDate": "2023-08-20",
            "endingDate": "2023-09-20",
            "startingBalance": 100.50,
            "totalPlayers": 50,
            "cowPrice": 15,
            "milkPrice": 10,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 123,
            "belowCapacityHealthUpdateStrategy": "Linear",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 1,
            "name": "Anika's Commons",
            "day": 5,
            "startingDate": "2023-08-20",
            "endingDate": "2023-09-20",
            "startingBalance": 200.50,
            "totalPlayers": 50,
            "cowPrice": 15,
            "milkPrice": 10,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 42,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        }
    ],
    oneCommons:
        [
            {
                "id": 1,
                "name": "Anika's Commons",
                "day": 5,
                "startingDate": "2023-08-20",
                "endingDate": "2023-09-20",
                "startingBalance": 2000.50,
                "totalPlayers": 50,
                "cowPrice": 15,
                "milkPrice": 10,
                "degradationRate": .5,
                "showLeaderboard": true,
                "carryingCapacity": 314,
                "belowCapacityHealthUpdateStrategy": "Constant",
                "aboveCapacityHealthUpdateStrategy": "Linear"
            }
        ],

    sevenCommons: [
        {
            "id": 10,
            "name": "Seths Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 8,
            "name": "Kevin's Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 6,
            "name": "Anika's Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 5,
            "name": "Evan's Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 4,
            "name": "Joshua's Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 3,
            "name": "Danny's Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        },
        {
            "id": 2,
            "name": "Jackson's Commons",
            "day": 5,
            "totalPlayers": 50,
            "cowPrice": 15,
            "degradationRate": .5,
            "showLeaderboard": true,
            "carryingCapacity": 100,
            "belowCapacityHealthUpdateStrategy": "Constant",
            "aboveCapacityHealthUpdateStrategy": "Linear"
        }
    ],
}

export default commonsFixtures;
